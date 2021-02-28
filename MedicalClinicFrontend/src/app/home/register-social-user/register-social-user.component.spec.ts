import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { RecaptchaFormsModule, RecaptchaModule } from 'ng-recaptcha';
import { MockAuthenticationService } from 'src/app/mocks/mock-authentication.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { UserService } from 'src/app/services/user.service';
import { RegisterSocialUserComponent } from './register-social-user.component';

describe('RegisterSocialUserComponent', () => {
  let component: RegisterSocialUserComponent;
  let fixture: ComponentFixture<RegisterSocialUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterSocialUserComponent],
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
        { provide: AuthenticationService, useClass: MockAuthenticationService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterSocialUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('after call addSocialUser should be return ADDED', () => {
    component.addSocialUser();
    expect(component.answer.serverAnswer).toEqual('ADDED');
  });
  it('should be correct state resolvedReCaptcha', () => {
    component.resolvedReCaptcha('qwerty');
    expect(component.reCaptchaResponse).toEqual('qwerty');
    expect(component.reCaptchaVerify).toBeTruthy();
  });
  it('after call checkPeselAndSetGender', () => {
    spyOn(component, 'checkPeselAndSetGender');
    component.checkPeselAndSetGender('pesel');
    expect(component.checkPeselAndSetGender).toHaveBeenCalled();
  });
  it('after call convertImageToBase64', () => {
    spyOn(component, 'convertImageToBase64');
    component.convertImageToBase64('url');
    expect(component.convertImageToBase64).toHaveBeenCalled();
  });
});
