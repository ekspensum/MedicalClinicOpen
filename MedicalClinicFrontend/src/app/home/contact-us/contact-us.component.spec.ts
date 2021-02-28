import { AgmCoreModule } from '@agm/core';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { RecaptchaFormsModule, RecaptchaModule } from 'ng-recaptcha';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { HomeService } from 'src/app/services/home.service';
import { UserService } from 'src/app/services/user.service';
import { ContactUsComponent } from './contact-us.component';
import { MockHomeService } from 'src/app/mocks/mock-home.service';

describe('ContactUsComponent', () => {
  let component: ContactUsComponent;
  let fixture: ComponentFixture<ContactUsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContactUsComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        RecaptchaModule,
        RecaptchaFormsModule,
        AgmCoreModule.forRoot({
          apiKey: 'XXXXXXXXXXXXXXXXXXXXXX'
        }),
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
        { provide: HomeService, useClass: MockHomeService}
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContactUsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    component.reCaptchaResponse = "reCaptchaResponse";
    component.reCaptchaVerify = true;
    expect(component).toBeTruthy();
  });
  it('after call sendEmail should be return SENT_EMAIL', ()=> {
    component.sendEmail();
    expect(component.answer.serverAnswer).toEqual('SENT_EMAIL');
  });
  it('tracks that function was called and tracks the argument of its call', () => {
    let eventFile = new Event('input');
    spyOn(component, 'onFileSelect');
    component.onFileSelect(eventFile);
    expect(component.onFileSelect).toHaveBeenCalled();
    expect(component.onFileSelect).toHaveBeenCalledWith(eventFile);
  });
  it('should be correct state onClearFileAttachments', () => {
    component.onClearFileAttachments();
    expect(component.contactUs.attachmentList.length).toEqual(0);
  });
  it('should be correct state resolvedReCaptcha', () => {
    component.resolvedReCaptcha('qwerty');
    expect(component.reCaptchaResponse).toEqual('qwerty');
    expect(component.reCaptchaVerify).toBeTruthy();
  });
  it('should be correct state onSentMail', () => {
    component.onSentMail();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.contactUs.attachmentList.length).toEqual(0);
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.contactUs.subject).toEqual('');
    expect(component.contactUs.message).toEqual('');
    expect(component.contactUs.replyEmail).toEqual('');
  });
});
