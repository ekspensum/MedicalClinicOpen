import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

import { SetDoctorFreeDaysComponent } from './set-doctor-free-days.component';

describe('SetDoctorFreeDaysComponent', () => {
  let component: SetDoctorFreeDaysComponent;
  let fixture: ComponentFixture<SetDoctorFreeDaysComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SetDoctorFreeDaysComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SetDoctorFreeDaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('doctor list length should be 1', () => {
    expect(component.doctorList.length).toEqual(1);
  });
  it('shuold be correct state on addDoctorFreeDayOnSubmit', ()=> {
    component.selectedIndex = 0;
    let doctorFreDay = new Date().toISOString().slice(0, 10)
    component.doctorFreeDayForm = new FormGroup({
      doctorFreeDay: new FormControl(doctorFreDay)
    });
    component.addDoctorFreeDayOnSubmit();
    expect(component.doctorFreeDayList.length).toEqual(2);
    doctorFreDay = new Date(0).toISOString().slice(0, 10)
    component.doctorFreeDayForm = new FormGroup({
      doctorFreeDay: new FormControl(doctorFreDay)
    });
    component.addDoctorFreeDayOnSubmit();
    expect(component.answer.serverAnswer).toEqual('PAST_DATE');
  });
  it('shuold be correct state on deleteFreeDay', ()=> {
    component.selectedIndex = 0;
    component.deleteFreeDay();
    expect(component.answer.serverAnswer).toEqual('FREE_DAY_REMOVED');
  });
  it('Doctor free days list length should be 2', () => {
    component.getDoctorFreeDays();
    expect(component.doctorFreeDayList.length).toEqual(2);
  });
});
