import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockAuthenticationService } from 'src/app/mocks/mock-authentication.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { UserService } from 'src/app/services/user.service';
import { AddPatientByEmployeeComponent } from './add-patient-by-employee.component';

describe('AddPatientByEmployeeComponent', () => {
  let component: AddPatientByEmployeeComponent;
  let fixture: ComponentFixture<AddPatientByEmployeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddPatientByEmployeeComponent ],
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
        { provide: UserService, useClass: MockUserService },
        { provide: AuthenticationService, useClass: MockAuthenticationService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPatientByEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('after call addPatient should be return ADDED', ()=> {
    component.patientForm = new FormGroup({
      username: new FormControl('username', [Validators.required, Validators.pattern(component.patterns.login)]),
      firstName: new FormControl('firstName', [Validators.required, Validators.pattern(component.patterns.name)]),
      lastName: new FormControl('lastName', [Validators.required, Validators.pattern(component.patterns.name)]),
      country: new FormControl('country', [Validators.required, Validators.pattern(component.patterns.name)]),
      zipCode: new FormControl('00-001', [Validators.required, Validators.pattern(component.patterns.zipCode)]),
      city: new FormControl('city', [Validators.required, Validators.pattern(component.patterns.name)]),
      street: new FormControl('street', [Validators.required, Validators.pattern(component.patterns.name)]),
      streetNo: new FormControl('streetNo', [Validators.required, Validators.pattern(component.patterns.smalName)]),
      unitNo: new FormControl('unitNo', Validators.pattern(component.patterns.smalNameFromZero)),
      pesel: new FormControl('72030378826', Validators.required),
      gender: new FormControl({value: '', disabled: true}, Validators.required),
      phone: new FormControl('123456789', [Validators.required, Validators.pattern(component.patterns.phone)]),
      email: new FormControl('email@email.com', [Validators.required, Validators.pattern(component.patterns.email)]),
      language: new FormControl(),
    });
    component.addPatient();
    expect(component.answer.serverAnswer).toEqual('ADDED');
    expect(component.patient.user.username).toEqual('username');
    expect(component.patient.user.firstName).toEqual('firstName');
    expect(component.patient.user.lastName).toEqual('lastName');
    expect(component.patient.country).toEqual('country');
    expect(component.patient.zipCode).toEqual('00-001');
    expect(component.patient.city).toEqual('city');
    expect(component.patient.street).toEqual('street');
    expect(component.patient.streetNo).toEqual('streetNo');
    expect(component.patient.unitNo).toEqual('unitNo');
    expect(component.patient.pesel).toEqual('72030378826');
    expect(component.patient.phone).toEqual('123456789');
    expect(component.patient.user.email).toEqual('email@email.com');
  });
  it('shuold be correct state onCorrectAddPatient', () => {
    component.onCorrectAddPatient();
    expect(component.patient.user.firstName).toBeUndefined();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
  });
});
