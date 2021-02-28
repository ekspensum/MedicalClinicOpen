import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Translate } from 'src/app/interfaces/translate';
import { DoctorFreeDay } from 'src/app/models/doctor-free-day';
import { DoctorFreeDayData } from 'src/app/models/doctor-free-day-data';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

@Component({
  selector: 'app-self-set-doctor-free-days',
  templateUrl: './self-set-doctor-free-days.component.html',
  styleUrls: ['./self-set-doctor-free-days.component.css']
})
export class SelfSetDoctorFreeDaysComponent implements OnInit {

  translate: Translate;
  answer: UserServiceAnswer;
  doctorFreeDay: DoctorFreeDay;
  doctorFreeDayList: Array<DoctorFreeDay>;
  doctorFreeDayForm: FormGroup;
  doctorFreeDayId: number;
  doctorFreeDayData: DoctorFreeDayData;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService) {
    this.doctorFreeDayForm = new FormGroup({
      doctorFreeDay: new FormControl()
    });
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
    this.doctorFreeDayData = new DoctorFreeDayData();
  }

  ngOnInit(): void {
    this.getDoctorFreeDays();
  }

  addDoctorFreeDayOnSubmit(): void {
    this.doctorFreeDayData.doctorFreeDay = this.doctorFreeDayForm.get('doctorFreeDay').value;
    const dateNow = new Date().toISOString().slice(0, 10);
    if(dateNow > this.doctorFreeDayData.doctorFreeDay.toString()){
      this.answer = this.userService.switchAnswer('PAST_DATE');
      return;
    }
    this.doctorFreeDayData.doctorUsername = sessionStorage.getItem('username');
    this.visitService.addDoctorFreeDayByDoctor(this.doctorFreeDayData).subscribe(
      (response: HttpResponse<any>) => {
        this.doctorFreeDayList = response.body;
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
    this.doctorFreeDayForm.reset();
  }
  getDoctorFreeDays(): void {
    this.visitService.getDoctorFreeDayListByDoctor(sessionStorage.getItem('username')).subscribe(
      (response: HttpResponse<any>) => {
        this.doctorFreeDayList = response.body;
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
  }
  deleteFreeDay(): void {
    this.visitService.removeDoctorFreeDay(this.doctorFreeDayId).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
        this.ngOnInit();
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
    this.doctorFreeDayId = null;
  }
}
