import { Component, OnInit } from '@angular/core';
import { Translate } from 'src/app/interfaces/translate';
import { FileAttachment } from 'src/app/models/file-attachment';
import { Patterns } from 'src/app/models/patterns';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { Doctor } from 'src/app/models/doctor';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { NgxImageCompressService } from 'ngx-image-compress';

@Component({
  selector: 'app-self-edit-data-doctor',
  templateUrl: './self-edit-data-doctor.component.html',
  styleUrls: ['./self-edit-data-doctor.component.css']
})
export class SelfEditDataDoctorComponent implements OnInit {

  doctor: Doctor
  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  passwordRepet: string;
  languageCodeList: Array<string>;
  selectedFile: File;
  oryginalFileSize: number = 0;
  compressedFileSize: number = 0;
  fileAttachment: FileAttachment;
  user_photo: any;
  isValidPesel: boolean;

  constructor(private userService: UserService, private languageService: LanguageService, private authService: AuthenticationService,
    private imageCompress: NgxImageCompressService) {
    this.translate = languageService.loadLanguage();
    this.languageCodeList = languageService.serveLanguages;
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
  }

  ngOnInit(): void {
    this.userService.getDoctor(sessionStorage.getItem('username')).subscribe(
      (userData: HttpResponse<any>) => {
        this.doctor = userData.body;
        if(this.doctor.user.photo){
          this.user_photo = 'data:image/png;base64,' + this.doctor.user.photo;
        }
        this.checkPesel(this.doctor.pesel);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }

  onFileSelect(event) {
    this.selectedFile = event.target.files[0];
    let fileReader = new FileReader();
    fileReader.onload = (event: any) => {
      this.fileAttachment = new FileAttachment();
      this.fileAttachment.fileName = this.selectedFile.name;
      this.fileAttachment.fileType = this.selectedFile.type;
      this.oryginalFileSize = this.selectedFile.size;
      let size = 30000;
      let ratio = size / this.oryginalFileSize * 100 < 9 ? 9 : size / this.oryginalFileSize * 100 > 100 ? 100 : size / this.oryginalFileSize * 100;
      this.imageCompress.compressFile(event.target.result, 0, ratio, 75).then(
        result => {
          this.user_photo = result;
          this.doctor.user.photoString = result;
          this.compressedFileSize = this.imageCompress.byteCount(result);
          this.fileAttachment.fileSize = this.compressedFileSize;
        }
      );
    }
    fileReader.readAsDataURL(this.selectedFile);
  }
  onClearFileAttachment() {
    this.doctor.user.photoString = null;
    this.fileAttachment = null;
    this.oryginalFileSize = 0;
    this.compressedFileSize = 0;
    if(this.doctor.user.photo){
      this.user_photo = 'data:image/png;base64,' + this.doctor.user.photo;
    } else {
      this.user_photo = null;
    }
  }
  deletePhoto(){
    if (confirm(this.translate.user.confirmRemove)) {
      this.userService.removeUserPhoto(sessionStorage.getItem('username')).subscribe(
        (response: HttpResponse<any>) => {
          this.answer = this.userService.switchAnswer(response.body);
          this.user_photo = null;
        },
        (error: HttpErrorResponse) => {
          this.answer = this.userService.switchAnswer(error.status);
        }
      );
    }
  }
  checkUsernameAndUpdate(){
    if(this.doctor.user.username !== sessionStorage.getItem('username')){
      // logout is necessary when user change username
      if(!confirm(this.translate.login.logoutDifferentUsername)){
        return;
      } else {
        this.updateDoctor();
        this.authService.logOut();
      }
    } else {
      this.updateDoctor();
    }
  }
  updateDoctor() {
    this.userService.selfUpdateDoctor(this.doctor).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  checkPesel(pesel: string) {
    this.isValidPesel = true;
    if(pesel){
      this.isValidPesel = this.userService.decodePesel(pesel).valid;
    }
  }
  onCorrectUpdate() {
    this.answer.serverAnswer = null;
    this.answer.visableForm = true;
    this.answer.notunique = false;
    this.doctor.user.photoString = null;
    this.fileAttachment = null;
    this.ngOnInit();
  }
}
