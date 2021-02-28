import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { VisitStatus } from 'src/app/enums/visit-status.enum';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { Visit } from 'src/app/models/visit';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

import { MakeDiagnosisComponent } from './make-diagnosis.component';

describe('MakeDiagnosisComponent', () => {
  let component: MakeDiagnosisComponent;
  let fixture: ComponentFixture<MakeDiagnosisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MakeDiagnosisComponent ],
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
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MakeDiagnosisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('Doctor visits list length should be 3', () => {
    let dateFromSearch = new Date().toISOString().slice(0, 10)
    let dateToSearch = new Date().toISOString().slice(0, 10)
    component.getDoctorVisits(dateFromSearch, dateToSearch, VisitStatus.Planned);
    expect(component.visitList.length).toEqual(3);
  });
  it('shuold be correct state on selectVisit', () => {
    component.selectedVisitId = 1;
    component.selectVisit();
    expect(component.referralFormArray.length).toEqual(1);
  });
  it('shuold be correct state on addReferral', () => {
    component.referralFormArray.clear();
    component.addReferral();
    expect(component.referralFormArray.length).toEqual(1);
  });
  it('shuold be correct state on removeReferral', () => {
    component.referralFormArray.clear();
    component.addReferral();
    component.addReferral();
    component.removeReferral(1);
    expect(component.referralFormArray.length).toEqual(1);
  });
  it('shuold be correct state on onSubmitMakeDiagnosis', ()=> {
    component.visit = new Visit();
    component.onSubmitMakeDiagnosis();
    expect(component.answer.serverAnswer).toEqual('ADDED_DIAGNOSIS');
  });
});
