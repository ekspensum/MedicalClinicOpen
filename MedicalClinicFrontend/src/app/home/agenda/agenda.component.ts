import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Translate } from 'src/app/interfaces/translate';
import { Doctor } from 'src/app/models/doctor';
import { UserServiceAnswer } from 'src/app/models/user-service-answer';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

@Component({
  selector: 'app-agenda',
  templateUrl: './agenda.component.html',
  styleUrls: ['./agenda.component.css']
})
export class AgendaComponent implements OnInit {

  translate: Translate;
  selectedIndex: number = -1;
  doctorList: Array<Doctor>;
  answer: UserServiceAnswer;
  dayFrom: number = 0;
  dayTo: number = 7;

  constructor(private userService: UserService, private languageService: LanguageService, private visitService: VisitService) {
    this.translate = languageService.loadLanguage();
    this.answer = new UserServiceAnswer();
   }

  ngOnInit(): void {
    this.getAllDoctors();
  }

  getAllDoctors(): void {
    this.visitService.readAllDoctorsForAgendaPage(this.dayFrom, this.dayTo).subscribe(
      (response: HttpResponse<any>) => {
        this.doctorList = response.body;
        this.doctorList.sort((a, b) => a.user.lastName.localeCompare(b.user.lastName));
        this.visitService.removeNotWorkingHours(this.doctorList);
      },
      (error: HttpErrorResponse) => {
        this.answer = this.userService.switchAnswer(error.status);
      }
     );
  }
  translateDayOfWeek(dayOfWeek: any): string {
    return this.userService.switchDayOfWeek(dayOfWeek);
  }
  goToNextWeek(): void {
    this.dayFrom = this.dayFrom + 7;
    this.dayTo = this.dayTo + 7;
    this.ngOnInit();
  }
  goToPreviousWeek(): void {
    this.dayFrom = this.dayFrom - 7;
    this.dayTo = this.dayTo - 7;
    this.ngOnInit();
  }
 }
