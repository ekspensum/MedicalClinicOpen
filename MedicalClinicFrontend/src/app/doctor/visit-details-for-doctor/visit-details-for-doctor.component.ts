import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Translate } from 'src/app/interfaces/translate';
import { MedicalDocument } from 'src/app/models/medical-document';
import { Patterns } from 'src/app/models/patterns';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { Visit } from 'src/app/models/visit';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

@Component({
  selector: 'app-visit-details-for-doctor',
  templateUrl: './visit-details-for-doctor.component.html',
  styleUrls: ['./visit-details-for-doctor.component.css']
})
export class VisitDetailsForDoctorComponent implements OnInit {

  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  visit: Visit;
  enabledMedicalDocList: Array<MedicalDocument>;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService) {
    this.translate = languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.enabledMedicalDocList = new Array<MedicalDocument>();
   }

  ngOnInit(): void {
    this.visitService.getVisit(this.visitService.visitIdForDetails).subscribe(
      (response: HttpResponse<any>) => {
        this.visit = response.body;
        this.enabledMedicalDocList.length = 0;
        for(let i=0; i<this.visit.patient.medicalDocumentList.length; i++){
          if(this.visit.patient.medicalDocumentList[i].enabled){
            this.enabledMedicalDocList.push(this.visit.patient.medicalDocumentList[i]);
          }
        }
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  onDisplayFile(medicalDocument: MedicalDocument){
    this.userService.getMedicalByteFile(medicalDocument.id).subscribe(
      (response: HttpResponse<any>) => {
        let blob:any = new Blob([response.body], { type: medicalDocument.fileType });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  translateVisitStatus(visitStatus: any) {
    return this.visitService.switchVisitStatus(visitStatus);
  }
}
