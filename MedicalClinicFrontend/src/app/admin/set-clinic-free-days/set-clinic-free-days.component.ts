import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Translate } from 'src/app/interfaces/translate';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { ClinicFreeDay } from 'src/app/models/clinic-free-day';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-set-clinic-free-days',
  templateUrl: './set-clinic-free-days.component.html',
  styleUrls: ['./set-clinic-free-days.component.css']
})
export class SetClinicFreeDaysComponent implements OnInit {

  translate: Translate;
  answer: UserServiceAnswer;
  clinicFreeDay: ClinicFreeDay;
  clinicFreeDayList: Array<ClinicFreeDay>;
  clinicFreeDayForm: FormGroup;
  clinicFreeDayId: number;

  constructor(public userService: UserService, private languageService: LanguageService, private visitService: VisitService) {
    this.clinicFreeDayForm = new FormGroup({
      clinicFreeDay: new FormControl()
    });
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
   }

  ngOnInit(): void {
    this.getClinicFreeDays();
  }

  addClinicFreeDayOnSubmit() {
    let clinicFreDay = this.clinicFreeDayForm.get('clinicFreeDay').value;
    let dateNow = new Date().toISOString().slice(0, 10)
    if(dateNow > clinicFreDay){
      this.answer = this.userService.switchAnswer('PAST_DATE');
      return;
    }
    this.visitService.addClinicFreeDay(clinicFreDay).subscribe(
      (response: HttpResponse<any>) => {
        this.clinicFreeDayList = response.body;
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
    this.clinicFreeDayForm.reset();
  }
  getClinicFreeDays(){
    this.visitService.getClinicFreeDayList().subscribe(
      (response: HttpResponse<any>) => {
        this.clinicFreeDayList = response.body;
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
  }
  deleteFreeDay(){
    this.visitService.removeClinicFreeDay(this.clinicFreeDayId).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
        this.ngOnInit();
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      });
      this.clinicFreeDayId = null;
  }
}
