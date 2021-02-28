import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { LanguageService } from 'src/app/services/language.service';
import { UserService } from 'src/app/services/user.service';
import { AddDoctorComponent } from './add-doctor.component';

describe('AddDoctorComponent', () => {
  let component: AddDoctorComponent;
  let fixture: ComponentFixture<AddDoctorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddDoctorComponent],
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
      providers: [TranslateService, HttpClient, UserService, LanguageService,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDoctorComponent);
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
  it('after call addDoctor should be return ADDED', ()=> {
    component.addDoctor();
    expect(component.answer.serverAnswer).toEqual('ADDED');
  });
  it('shuold be correct state onCorrectAddDoctor', () => {
    component.onCorrectAddDoctor();
    expect(component.doctor.user.firstName).toEqual('');
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
  });
});
