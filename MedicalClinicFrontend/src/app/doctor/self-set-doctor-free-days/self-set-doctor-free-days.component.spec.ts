import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';

import { SelfSetDoctorFreeDaysComponent } from './self-set-doctor-free-days.component';

describe('SelfSetDoctorFreeDaysComponent', () => {
  let component: SelfSetDoctorFreeDaysComponent;
  let fixture: ComponentFixture<SelfSetDoctorFreeDaysComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelfSetDoctorFreeDaysComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: VisitService, useClass: MockVisitService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfSetDoctorFreeDaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on addDoctorFreeDayOnSubmit', ()=> {
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
    component.deleteFreeDay();
    expect(component.answer.serverAnswer).toEqual('FREE_DAY_REMOVED');
  });
  it('Doctor free days list length should be 1', () => {
    component.getDoctorFreeDays();
    expect(component.doctorFreeDayList.length).toEqual(1);
  });
});
