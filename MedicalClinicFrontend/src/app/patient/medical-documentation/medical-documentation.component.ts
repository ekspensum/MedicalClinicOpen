import { Component, OnInit } from '@angular/core';
import { Translate } from 'src/app/interfaces/translate';
import { Patterns } from 'src/app/models/patterns';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { MedicalDocument } from 'src/app/models/medical-document';
import { MedicalByteFile } from 'src/app/models/medical-byte-file';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-medical-documentation',
  templateUrl: './medical-documentation.component.html',
  styleUrls: ['./medical-documentation.component.css']
})
export class MedicalDocumentationComponent implements OnInit {

  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  medicalDocument: MedicalDocument;
  medicalDocumentList: Array<MedicalDocument>;
  medicalByteFile: MedicalByteFile;
  medicalDocumentForm: FormGroup;
  medicalDocumentFormArray: FormArray;
  fileIdToRemove: string;
  errorFileExcced: boolean;
  errorFileMeesage: string;
  medicalDocListSaved: boolean = true;
  image: any;

  constructor(private userService: UserService, private languageService: LanguageService, private sanitizer: DomSanitizer) {
    this.translate = languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.medicalDocumentList = new Array<MedicalDocument>();
    this.medicalDocumentForm = new FormGroup({
      medicalDocInnerFormArray: new FormArray([])
    });
    this.medicalDocumentFormArray = this.medicalDocumentForm.get('medicalDocInnerFormArray') as FormArray;
   }

  ngOnInit(): void {
    this.userService.getPatient(sessionStorage.getItem('username')).subscribe(
      (userData: HttpResponse<any>) => {
        this.medicalDocumentFormArray.clear();
        this.medicalDocumentList = userData.body.medicalDocumentList;
        this.medicalDocumentList.sort((a, b) => a.registerDateTime.toString().localeCompare(b.registerDateTime.toString()));
        for (let i = 0; i < this.medicalDocumentList.length; i++) {
          const medicalDocumentForm = new FormGroup({
            description: new FormControl(this.medicalDocumentList[i].description, [Validators.pattern(this.patterns.description)]),
            enabled: new FormControl(this.medicalDocumentList[i].enabled)
          });
          this.medicalDocumentFormArray.push(medicalDocumentForm);
        }
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }

  onFileSelect(event): void {
    const files = event.target.files;
    if (files.length + this.medicalDocumentList.length > this.patterns.attachmentsNumber) {
      this.errorFileExcced = true;
      this.errorFileMeesage = this.translate.error.exceedFileNumber + this.patterns.attachmentsNumber;
      return;
    }
    let attachmentsSize = 0;
    for (let i = 0; i < this.medicalDocumentList.length; i++) {
      attachmentsSize += this.medicalDocumentList[i].fileSize;
    }
    for (let i = 0; i < files.length; i++) {
      attachmentsSize += files[i].size;
    }
    if (attachmentsSize > this.patterns.attachmentsSize) {
      this.errorFileExcced = true;
      this.errorFileMeesage = this.translate.error.exceedAllFilesSize + this.patterns.attachmentsSize;
      return;
    }

    for (let i = 0, j = this.medicalDocumentList.length;  i < files.length; i++, j++) {
      this.medicalDocument = new MedicalDocument();
      this.medicalDocument.medicalByteFile = new MedicalByteFile();
      this.medicalDocumentList.push(this.medicalDocument);

      const medicalDocumentForm = new FormGroup({
        description: new FormControl('', [Validators.pattern(this.patterns.description)]),
        enabled: new FormControl(true)
      });
      this.medicalDocumentFormArray.push(medicalDocumentForm);

      const fileReader = new FileReader();
      const selectedFile = event.target.files[i];
      fileReader.onload = (e) => {
        this.medicalDocumentList[j].medicalByteFile.fileBase64 = fileReader.result;
        this.medicalDocumentList[j].fileName = selectedFile.name;
        this.medicalDocumentList[j].fileType = selectedFile.type;
        this.medicalDocumentList[j].fileSize = selectedFile.size;
      }
      fileReader.readAsDataURL(selectedFile);
    }
    this.medicalDocListSaved = false;
  }
  onRemoveFile(fileIdToRemove: number): void {
    if (confirm(this.translate.user.confirmRemove)) {
      this.userService.removeMedicalDocument(fileIdToRemove).subscribe(
        (response: HttpResponse<any>) => {
          this.answer = this.userService.switchAnswer(response.body);
          this.ngOnInit();
        },
        (error: HttpErrorResponse) => {
          this.answer = this.userService.switchAnswer(error.status);
        }
      );
    }
  }
  updateMedicalDocumentation(): void {
    for (let i = 0; i < this.medicalDocumentList.length; i++){
      this.medicalDocumentList[i].description = this.medicalDocumentFormArray.controls[i].get('description').value;
      this.medicalDocumentList[i].enabled = this.medicalDocumentFormArray.controls[i].get('enabled').value;
    }
    this.userService.addOrUpdateMedicalDocumentation(this.medicalDocumentList).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
        this.ngOnInit();
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
    this.medicalDocListSaved = true;
  }
  onDisplayFile(medicalDocument: MedicalDocument): void {
    this.userService.getMedicalByteFile(medicalDocument.id).subscribe(
      (response: HttpResponse<any>) => {
        const blob: any = new Blob([response.body], { type: medicalDocument.fileType });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
}
