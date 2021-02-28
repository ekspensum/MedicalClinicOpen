import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Translate } from 'src/app/interfaces/translate';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { WorkingWeek } from 'src/app/models/working-week';

@Component({
  selector: 'app-self-edit-agenda-doctor',
  templateUrl: './self-edit-agenda-doctor.component.html',
  styleUrls: ['./self-edit-agenda-doctor.component.css']
})
export class SelfEditAgendaDoctorComponent implements OnInit {

  workingWeek: WorkingWeek;
  translate: Translate;
  answer: UserServiceAnswer;

  constructor(private userService: UserService, private languageService: LanguageService) {
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
   }

  ngOnInit(): void {
    this.userService.getDoctorWorkingWeek(sessionStorage.getItem('username')).subscribe(
      (userData: HttpResponse<any>) => {
        this.workingWeek = userData.body;
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  updateDoctorAgenda() {
    this.userService.setDoctorWorkingWeek(this.workingWeek).subscribe(
      (response: HttpResponse<any>) => {
        this.answer = this.userService.switchAnswer(response.body);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
    );
  }
  translateDayOfWeek(dayOfWeek: any) {
    return this.userService.switchDayOfWeek(dayOfWeek);
  }
}
