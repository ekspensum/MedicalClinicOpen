import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { VisitStatus } from 'src/app/enums/visit-status.enum';
import { Translate } from 'src/app/interfaces/translate';
import { Diagnosis } from 'src/app/models/diagnosis';
import { Patterns } from 'src/app/models/patterns';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { Visit } from 'src/app/models/visit';
import { VisitData } from 'src/app/models/visit-data';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { PdfService } from 'src/app/services/pdf.service';
import { Referral } from 'src/app/models/referral';
import { MedicalDocument } from 'src/app/models/medical-document';

@Component({
  selector: 'app-make-diagnosis',
  templateUrl: './make-diagnosis.component.html',
  styleUrls: ['./make-diagnosis.component.css']
})
export class MakeDiagnosisComponent implements OnInit {

  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  doctorUsername: string;
  visitDataRequest: VisitData;
  visitList: Array<Visit>;
  searchVisitForm: FormGroup;
  selectedVisitId: number;
  dayFrom: number = 0;
  dayTo: number = 7;
  foundZeroVisits: boolean;
  visit: Visit;
  diagnosis: Diagnosis;
  visitStatusList: Array<VisitStatus>;
  makeDiagnosisForm: FormGroup;
  referralFormArray: FormArray;
  enabledMedicalDocList: Array<MedicalDocument>;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService,
    private pdfService: PdfService) {
    this.translate = languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.doctorUsername = sessionStorage.getItem('username');
    this.visitStatusList = [VisitStatus.Planned, VisitStatus.Confirmed, VisitStatus.Completed];
    this.visit = null;
    let dateFrom = new Date();
    dateFrom.setDate(dateFrom.getDate() + this.dayFrom);
    let dateTo = new Date();
    dateTo.setDate(dateTo.getDate() + this.dayTo);
    this.searchVisitForm = new FormGroup({
      dateFrom: new FormControl(dateFrom.toISOString().slice(0, 10)),
      dateTo: new FormControl(dateTo.toISOString().slice(0, 10)),
      visitStatus: new FormControl(VisitStatus.Confirmed)
    });
    this.makeDiagnosisForm = new FormGroup({
      ailments: new FormControl('', [Validators.required, Validators.pattern(this.patterns.description)]),
      orders: new FormControl('', [Validators.required, Validators.pattern(this.patterns.largeDescription)]),
      medicines: new FormControl('', [Validators.required, Validators.pattern(this.patterns.description)]),
      referralList: new FormArray([])
    });
    this.referralFormArray = this.makeDiagnosisForm.get('referralList') as FormArray;
    this.enabledMedicalDocList = new Array<MedicalDocument>();
   }

  ngOnInit(): void {
  }

  onSubmitSearchVisit(){
    this.visit = null;
    this.getDoctorVisits(this.searchVisitForm.get('dateFrom').value, this.searchVisitForm.get('dateTo').value, this.searchVisitForm.get('visitStatus').value);
  }
  getDoctorVisits(dateFrom: string, dateTo: string, visitStatus: VisitStatus){
    this.visitDataRequest = new VisitData(null, null, this.doctorUsername, null, null, null, dateFrom, dateTo, visitStatus);
    this.visitService.getDoctorVisitsByDateAndStatus(this.visitDataRequest).subscribe((response: HttpResponse<any>) => {
      this.visitList = response.body;
      this.visitList.sort((a, b) => a.visitDateTime.toString().localeCompare(b.visitDateTime.toString()));
      if(this.visitList.length === 0){
        this.foundZeroVisits = true;
      }
    },
    (error: HttpErrorResponse) => {
      this.answer = this.userService.switchAnswer(error.status);
    }
   );
  }
  selectVisit(){
    this.visitService.getVisit(this.selectedVisitId).subscribe((response: HttpResponse<any>) => {
      this.visit = response.body;
      this.enabledMedicalDocList.length = 0;
      for(let i=0; i<this.visit.patient.medicalDocumentList.length; i++){
        if(this.visit.patient.medicalDocumentList[i].enabled){
          this.enabledMedicalDocList.push(this.visit.patient.medicalDocumentList[i]);
        }
      }
      this.makeDiagnosisForm.reset();
      this.referralFormArray.clear();
      if(this.visit.diagnosis){
        this.makeDiagnosisForm = new FormGroup({
          ailments: new FormControl(this.visit.diagnosis.ailments, [Validators.required, Validators.pattern(this.patterns.description)]),
          orders: new FormControl(this.visit.diagnosis.orders, [Validators.required, Validators.pattern(this.patterns.largeDescription)]),
          medicines: new FormControl(this.visit.diagnosis.medicines, [Validators.required, Validators.pattern(this.patterns.description)]),
          referralList: new FormArray([])
        });
        this.referralFormArray = this.makeDiagnosisForm.get('referralList') as FormArray;
        for(let i=0; i< this.visit.diagnosis.referralList.length; i++){
          const referral = new FormGroup({
            id: new FormControl(this.visit.diagnosis.referralList[i].id),
            recognition: new FormControl(this.visit.diagnosis.referralList[i].recognition, [Validators.required, Validators.pattern(this.patterns.description)]),
            scopeOfExam: new FormControl(this.visit.diagnosis.referralList[i].scopeOfExam, [Validators.required, Validators.pattern(this.patterns.description)]),
            researchUnit: new FormControl(this.visit.diagnosis.referralList[i].researchUnit, [Validators.required, Validators.pattern(this.patterns.description)]),
            registerDateTime: new FormControl(this.visit.diagnosis.referralList[i].registerDateTime),
          });
          this.referralFormArray.push(referral);
        }
      }
    },
    (error: HttpErrorResponse) => {
      this.answer = this.userService.switchAnswer(error.status);
    });
  }
  addReferral(){
    const referral = new FormGroup({
      recognition: new FormControl('', [Validators.required, Validators.pattern(this.patterns.description)]),
      scopeOfExam: new FormControl('', [Validators.required, Validators.pattern(this.patterns.description)]),
      researchUnit: new FormControl('', [Validators.required, Validators.pattern(this.patterns.description)])
    });
    this.referralFormArray.push(referral);
  }
  removeReferral(index: number){
    this.referralFormArray.removeAt(index);
  }
  onSubmitMakeDiagnosis() {
    if(!this.visit.diagnosis){
      this.visit.diagnosis = new Diagnosis();
    }
    this.visit.diagnosis.ailments = this.makeDiagnosisForm.get('ailments').value;
    this.visit.diagnosis.orders = this.makeDiagnosisForm.get('orders').value;
    this.visit.diagnosis.medicines = this.makeDiagnosisForm.get('medicines').value;
    this.visit.diagnosis.referralList = this.makeDiagnosisForm.get('referralList').value;
    this.visitService.makeDiagnosis(this.visit).subscribe((response: HttpResponse<any>) => {
      this.answer = this.userService.switchAnswer(response.body);
    },
    (error: HttpErrorResponse) => {
      this.answer = this.userService.switchAnswer(error.status);
    });
  }
  generateDiagnosisPdf(){
    this.onSubmitMakeDiagnosis();
    this.pdfService.generateDiagnosisPdf(this.visit);
  }
  generateReferralPdf(referral: Referral){
    this.onSubmitMakeDiagnosis();
    this.pdfService.generateReferralPdf(referral, this.visit);
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
