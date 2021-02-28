import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgxImageCompressService } from 'ngx-image-compress';
import { Translate } from 'src/app/interfaces/translate';
import { FileAttachment } from 'src/app/models/file-attachment';
import { Patient } from 'src/app/models/patient';
import { Patterns } from 'src/app/models/patterns';
import { User } from 'src/app/models/user';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { HomeService } from 'src/app/services/home.service';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-register-patient',
  templateUrl: './register-patient.component.html',
  styleUrls: ['./register-patient.component.css']
})
export class RegisterPatientComponent implements OnInit {

  user: User;
  patient: Patient;
  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  selectedFile: File;
  oryginalFileSize: number = 0;
  compressedFileSize: number = 0;
  fileAttachment: FileAttachment;
  user_photo: any;
  languageCodeList: Array<string>;
  reCaptchaVerify: boolean;
  reCaptchaResponse: string;
  isValidPesel: boolean;

  constructor(private userService: UserService, private languageService: LanguageService, private homeService: HomeService,
    private translateService: TranslateService, private imageCompress: NgxImageCompressService) {
    this.patterns = new Patterns();
    this.user = new User();
    this.patient = new Patient(this.user);
    this.translate = languageService.loadLanguage();
    this.languageCodeList = languageService.serveLanguages;
    this.patient.user.language = this.translateService.getBrowserLang();
    this.answer = new UserServiceAnswer();
   }

  ngOnInit(): void {
  }

  addPatient() {
    this.userService.savePatientByRegistrationPage(this.patient, this.reCaptchaResponse).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    )
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
          this.patient.user.photoString = result;
          this.compressedFileSize = this.imageCompress.byteCount(result);
          this.fileAttachment.fileSize = this.compressedFileSize;
        }
      );
    }
    fileReader.readAsDataURL(this.selectedFile);
  }
  onClearFileAttachment(){
    this.patient.user.photoString = null;
    this.fileAttachment = null;
    this.oryginalFileSize = 0;
    this.compressedFileSize = 0;
    this.user_photo = null;
  }
  resolvedReCaptcha(captchaResponse: string) {
    this.reCaptchaResponse = captchaResponse;
    this.reCaptchaVerify = true;
  }
  checkPeselAndSetGender(pesel: string) {
    this.isValidPesel = this.userService.decodePesel(pesel).valid;
    this.patient.gender = this.userService.decodePesel(pesel).gender;
  }
  onCorrectAddPatient(){
    this.answer.serverAnswer = null;
    this.answer.visableForm = true;
    this.answer.notunique = false;
    this.reCaptchaVerify = false;
    this.patient.user.username = '';
    this.patient.user.firstName = '';
    this.patient.user.lastName = '';
    this.patient.country = '';
    this.patient.zipCode = '';
    this.patient.city = '';
    this.patient.street = '';
    this.patient.streetNo = '';
    this.patient.unitNo = '';
    this.patient.pesel = '';
    this.patient.phone = '';
    this.patient.user.email = '';
    this.patient.user.language = this.translateService.getBrowserLang();
    this.onClearFileAttachment();
  }
}
