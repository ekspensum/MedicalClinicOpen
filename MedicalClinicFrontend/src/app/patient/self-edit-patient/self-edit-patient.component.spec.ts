import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { NgxImageCompressService } from 'ngx-image-compress';
import { MockAuthenticationService } from 'src/app/mocks/mock-authentication.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { Patient } from 'src/app/models/patient';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { SelfEditPatientComponent } from './self-edit-patient.component';

describe('SelfEditPatientComponent', () => {
  let component: SelfEditPatientComponent;
  let fixture: ComponentFixture<SelfEditPatientComponent>;
  let authService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelfEditPatientComponent],
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
        { provide: UserService, useClass: MockUserService },
        NgxImageCompressService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfEditPatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(MockAuthenticationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should be correct patient username from MockUserService', async(() => {
    fixture.whenStable().then(() => {
      const userNameInput: HTMLInputElement = fixture.nativeElement.querySelector('#username');
      expect(component.patient.user.username).toEqual(userNameInput.value);
    });
  }));
  it('tracks that function was called and tracks the argument of its call', () => {
    let eventFile = new Event('input');
    spyOn(component, 'onFileSelect');
    component.onFileSelect(eventFile);
    expect(component.onFileSelect).toHaveBeenCalled();
    expect(component.onFileSelect).toHaveBeenCalledWith(eventFile);
  });
  it('shuold be correct state onClearFileAttachment', () => {
    let user = new User();
    user.photo = null;
    component.patient = new Patient(user);
    component.onClearFileAttachment();
    expect(component.fileAttachment).toBeNull();
    expect(component.patient.user.photoString).toBeNull();
    expect(component.user_photo).toBeNull();
    user.photo = 'photo';
    component.onClearFileAttachment();
    expect(component.user_photo).toEqual('data:image/png;base64,' + user.photo);
  });
  it('should call checkUsernameAndUpdate', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(component, 'checkUsernameAndUpdate');
    component.checkUsernameAndUpdate();
    expect(component.checkUsernameAndUpdate).toHaveBeenCalled();
    expect(authService.logOut).toBeDefined();
  });
  it('after call updatePatient should be return UPDATED', () => {
    component.updatePatient();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('should call checkPeselAndSetGender', () => {
    spyOn(component, 'checkPeselAndSetGender');
    component.checkPeselAndSetGender('pesel');
    expect(component.checkPeselAndSetGender).toHaveBeenCalled();
  });
  it('should be correct state onCorrectUpdate', () => {
    component.onCorrectUpdate();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
    expect(component.patient.user.photoString).toBeNull();
    expect(component.fileAttachment).toBeNull();
  });
});
