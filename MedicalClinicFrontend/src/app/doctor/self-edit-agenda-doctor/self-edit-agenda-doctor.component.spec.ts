import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { SelfEditAgendaDoctorComponent } from './self-edit-agenda-doctor.component';

describe('SelfEditAgendaDoctorComponent', () => {
  let component: SelfEditAgendaDoctorComponent;
  let fixture: ComponentFixture<SelfEditAgendaDoctorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelfEditAgendaDoctorComponent ],
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
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfEditAgendaDoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on updateDoctorAgenda', ()=> {
    component.updateDoctorAgenda();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
});
