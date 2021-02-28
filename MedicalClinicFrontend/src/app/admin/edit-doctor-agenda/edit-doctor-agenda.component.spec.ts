import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';

import { EditDoctorAgendaComponent } from './edit-doctor-agenda.component';

describe('EditDoctorAgendaComponent', () => {
  let component: EditDoctorAgendaComponent;
  let fixture: ComponentFixture<EditDoctorAgendaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditDoctorAgendaComponent ],
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
    fixture = TestBed.createComponent(EditDoctorAgendaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('doctor list length should be 1', () => {
    expect(component.doctorList.length).toEqual(1);
  });
  it('shuold be correct state on assignUserData', ()=> {
    component.selectedIndex = 0;
    component.assignDoctorAgenda();
    expect(component.workingWeek).not.toBe(null);
  });
  it('after call updateDoctorAgenda should be return UPDATED', ()=> {
    component.selectedIndex = 0;
    component.updateDoctorAgenda();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('shuold be correct day of week', ()=> {
    expect(component.translateDayOfWeek('MONDAY')).toBe('Monday');
  });
});
