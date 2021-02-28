import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Translate } from 'src/app/interfaces/translate';
import { Doctor } from 'src/app/models/doctor';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { DoctorFreeDay } from 'src/app/models/doctor-free-day';
import { DoctorFreeDayData } from 'src/app/models/doctor-free-day-data';

@Component({
  selector: 'app-set-doctor-free-days',
  templateUrl: './set-doctor-free-days.component.html',
  styleUrls: ['./set-doctor-free-days.component.css']
})
export class SetDoctorFreeDaysComponent implements OnInit {

  selectedIndex: number = -1;
  doctorList: Array<Doctor>;
  translate: Translate;
  answer: UserServiceAnswer;
  doctorFreeDay: DoctorFreeDay;
  doctorFreeDayList: Array<DoctorFreeDay>;
  filteredDoctorFreeDayList: Array<DoctorFreeDay>;
  doctorFreeDayForm: FormGroup;
  doctorFreeDayId: number;
  doctorFreeDayData: DoctorFreeDayData;
  idx: number = 0;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService) {
    this.doctorFreeDayForm = new FormGroup({
      doctorFreeDay: new FormControl()
    });
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
    this.doctorFreeDayData = new DoctorFreeDayData();
    this.filteredDoctorFreeDayList = new Array<DoctorFreeDay>();
   }

  ngOnInit(): void {
    this.getAllDoctors();
  }

  getAllDoctors(): void {
    this.userService.readAllDoctors().subscribe(
      (response: HttpResponse<any>) => {
        this.doctorList = response.body;
        this.doctorList.sort((a, b) => a.user.lastName.localeCompare(b.user.lastName));
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
     );
  }
  addDoctorFreeDayOnSubmit(): void {
    this.doctorFreeDayData.doctorFreeDay = this.doctorFreeDayForm.get('doctorFreeDay').value;
    const dateNow = new Date().toISOString().slice(0, 10);
    if(dateNow > this.doctorFreeDayData.doctorFreeDay.toString()){
      this.answer = this.userService.switchAnswer('PAST_DATE');
      return;
    }
    this.doctorFreeDayData.doctorUsername = this.doctorList[this.selectedIndex].user.username;
    this.visitService.addDoctorFreeDayByAdmin(this.doctorFreeDayData).subscribe(
      (response: HttpResponse<any>) => {
        this.doctorFreeDayList = response.body;
        this.doFilterDoctorFreeDays();
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
    this.doctorFreeDayForm.reset();
  }
  deleteFreeDay(): void {
  this.visitService.removeDoctorFreeDay(this.doctorFreeDayId).subscribe(
    (response: HttpResponse<any>) => {
      this.answer = this.userService.switchAnswer(response.body);
      this.getDoctorFreeDays();
    },
    (error: HttpErrorResponse) => {
      this.answer = this.userService.switchAnswer(error.status);
    });
  this.doctorFreeDayId = null;
  }
  getDoctorFreeDays(): void {
    this.visitService.getDoctorFreeDayListByAdmin().subscribe(
      (response: HttpResponse<any>) => {
        this.doctorFreeDayList = response.body;
        if(this.selectedIndex > -1){
          this.doFilterDoctorFreeDays();
        }
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
    }
  doFilterDoctorFreeDays(): void {
    this.filteredDoctorFreeDayList = [];
    for (let i = 0; i < this.doctorFreeDayList.length; i++) {
      if (this.doctorFreeDayList[i].doctor.id === this.doctorList[this.selectedIndex].id) {
        this.filteredDoctorFreeDayList.push(this.doctorFreeDayList[i]);
      }
    }
  }
}
