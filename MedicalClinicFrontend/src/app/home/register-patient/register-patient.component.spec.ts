import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { RecaptchaFormsModule, RecaptchaModule } from 'ng-recaptcha';
import { NgxImageCompressService } from 'ngx-image-compress';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { RegisterPatientComponent } from './register-patient.component';

describe('RegisterPatientComponent', () => {
  let component: RegisterPatientComponent;
  let fixture: ComponentFixture<RegisterPatientComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterPatientComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        RecaptchaModule,
        RecaptchaFormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService },
        NgxImageCompressService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterPatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('form filed username should be empty and should be name: username', () => {
    const hostElement = fixture.nativeElement;
    const usernameInput: HTMLInputElement = hostElement.querySelector('#username');
    expect(usernameInput.value).not.toBeFalse();
    expect(usernameInput.name).toEqual('username');
  });
  it('tracks that function was called and tracks the argument of its call', () => {
    let eventFile = new Event('input');
    spyOn(component, 'onFileSelect');
    component.onFileSelect(eventFile);
    expect(component.onFileSelect).toHaveBeenCalled();
    expect(component.onFileSelect).toHaveBeenCalledWith(eventFile);
  });
  it('after call addPatient should be return ADDED', ()=> {
    component.addPatient();
    expect(component.answer.serverAnswer).toEqual('ADDED');
  });
  it('shuold be correct state onClearFileAttachment', () => {
    component.onClearFileAttachment();
    expect(component.patient.user.photoString).toBeNull();
    expect(component.fileAttachment).toBeNull();
    expect(component.user_photo).toBeNull();
  });
  it('should be correct state resolvedReCaptcha', () => {
    component.resolvedReCaptcha('qwerty');
    expect(component.reCaptchaResponse).toEqual('qwerty');
    expect(component.reCaptchaVerify).toBeTruthy();
  });
  it('after call checkPeselAndSetGender', ()=> {
    spyOn(component, 'checkPeselAndSetGender');
    component.checkPeselAndSetGender('pesel');
    expect(component.checkPeselAndSetGender).toHaveBeenCalled();
  });
  it('shuold be correct state onCorrectAddPatient', () => {
    component.onCorrectAddPatient();
    expect(component.patient.user.firstName).toEqual('');
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
    expect(component.patient.user.photoString).toBeNull();
    expect(component.fileAttachment).toBeNull();
  });
});
