import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';

import { SetClinicFreeDaysComponent } from './set-clinic-free-days.component';

describe('SetClinicFreeDaysComponent', () => {
  let component: SetClinicFreeDaysComponent;
  let fixture: ComponentFixture<SetClinicFreeDaysComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SetClinicFreeDaysComponent ],
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
    fixture = TestBed.createComponent(SetClinicFreeDaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('Clinic free days list length should be 1', () => {
    expect(component.clinicFreeDayList.length).toEqual(1);
  });
  it('shuold be correct state on addClinicFreeDayOnSubmit', ()=> {
    component.addClinicFreeDayOnSubmit();
    expect(component.clinicFreeDayList.length).toEqual(2);
    let clinicFreDay = new Date(0).toISOString().slice(0, 10)
    component.clinicFreeDayForm = new FormGroup({
      clinicFreeDay: new FormControl(clinicFreDay)
    });
    component.addClinicFreeDayOnSubmit();
    expect(component.answer.serverAnswer).toEqual('PAST_DATE');
  });
  it('shuold be correct state on deleteFreeDay', ()=> {
    component.deleteFreeDay();
    expect(component.answer.serverAnswer).toEqual('FREE_DAY_REMOVED');
  });
});
