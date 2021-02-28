import { APP_BASE_HREF } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { SelfRemovePatientComponent } from './self-remove-patient.component';

describe('SelfRemovePatientComponent', () => {
  let component: SelfRemovePatientComponent;
  let fixture: ComponentFixture<SelfRemovePatientComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
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
      declarations: [ SelfRemovePatientComponent ],
      providers: [TranslateService, HttpClient,
        { provide: APP_BASE_HREF, useValue : '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfRemovePatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('after call orderRemoveMyAccount should be return ORDERED_REMOVE_PATIENT', () => {
    component.orderRemoveMyAccount();
    expect(component.answer.serverAnswer).toEqual('ORDERED_REMOVE_PATIENT');
  });
  it('after call cancelOrderRemoveMyAccount should be return CANCELED_OREDER_REMOVE_PATIENT', () => {
    component.cancelOrderRemoveMyAccount();
    expect(component.answer.serverAnswer).toEqual('CANCELED_OREDER_REMOVE_PATIENT');
  });
});
