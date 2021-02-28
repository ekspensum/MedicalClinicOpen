import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { Patient } from 'src/app/models/patient';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { RemovePatientComponent } from './remove-patient.component';

describe('RemovePatientComponent', () => {
  let component: RemovePatientComponent;
  let fixture: ComponentFixture<RemovePatientComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RemovePatientComponent],
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
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RemovePatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    let user = new User();
    user.enabled = true;
    user.photo = 'some photo';
    let patient = new Patient(user);
    component.patient = patient;
    component.patientList.push(patient);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('patient list length should be 1', () => {
    component.searchPatient();
    expect(component.patientList.length).toEqual(1);
  });
  it('shuold be correct state on selectPatient', () => {
    component.selectedIndex = 0;
    component.selectPatient();
    expect(component.patientList.length).toEqual(0);
    expect(component.selectedIndex).toBeNull();
  });
  it('after call removePatient should be return REMOVED', ()=> {
    spyOn(window, 'confirm').and.returnValue(true);
    component.removePatient();
    expect(component.answer.serverAnswer).toEqual('REMOVED');
  });
  it('shuold be correct state onCorrectAnswerServer', ()=> {
    component.onCorrectAnswerServer();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.patient).toBeNull();
  });
});
