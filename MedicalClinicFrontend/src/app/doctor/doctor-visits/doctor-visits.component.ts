import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Translate } from 'src/app/interfaces/translate';
import { Patterns } from 'src/app/models/patterns';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { Visit } from 'src/app/models/visit';
import { VisitData } from 'src/app/models/visit-data';
import { LanguageService } from 'src/app/services/language.service';
import { PdfService } from 'src/app/services/pdf.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

@Component({
  selector: 'app-doctor-visits',
  templateUrl: './doctor-visits.component.html',
  styleUrls: ['./doctor-visits.component.css']
})
export class DoctorVisitsComponent implements OnInit {

  translate: Translate;
  patterns: Patterns;
  answer: UserServiceAnswer;
  doctorUsername: string;
  visitDataRequest: VisitData;
  visitList: Array<Visit>;
  dateRangeForm: FormGroup;
  selectVisitId: number;
  dayFrom: number = 0;
  dayTo: number = 7;
  foundZeroVisits: boolean;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService,
    private router: Router, private pdfService: PdfService) {
    this.translate = languageService.loadLanguage();
    this.patterns = new Patterns();
    this.answer = new UserServiceAnswer();
    this.doctorUsername = sessionStorage.getItem('username');
    let dateFrom = new Date();
    dateFrom.setDate(dateFrom.getDate() + this.dayFrom);
    let dateTo = new Date();
    dateTo.setDate(dateTo.getDate() + this.dayTo);
    this.dateRangeForm = new FormGroup({
      dateFrom: new FormControl(dateFrom.toISOString().slice(0, 10)),
      dateTo: new FormControl(dateTo.toISOString().slice(0, 10))
    });
   }

  ngOnInit(): void {
  }

  searchDoctorVisits(): void {
    this.getDoctorVisits(this.dateRangeForm.get('dateFrom').value, this.dateRangeForm.get('dateTo').value);
  }
  getDoctorVisits(dateFrom: string, dateTo: string){
    this.visitDataRequest = new VisitData(null, null, this.doctorUsername, null, null, null, dateFrom, dateTo);
    this.visitService.getDoctorPlannedVisitsByDate(this.visitDataRequest).subscribe((response: HttpResponse<any>) => {
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
  selectVisit(): void {
    this.visitService.visitIdForDetails = this.selectVisitId;
    this.router.navigate(['/doctor/visitdetails']);
  }
  generateVisitListPdf(): void {
    this.pdfService.generateVisitListPdf(this.visitList, this.dateRangeForm.get('dateFrom').value, this.dateRangeForm.get('dateTo').value);
  }
  translateVisitStatus(visitStatus: any): any {
    return this.visitService.switchVisitStatus(visitStatus);
  }
}
