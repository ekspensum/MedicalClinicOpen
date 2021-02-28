import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

import { ScheduleVisitComponent } from './schedule-visit.component';

describe('ScheduleVisitComponent', () => {
  let component: ScheduleVisitComponent;
  let fixture: ComponentFixture<ScheduleVisitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScheduleVisitComponent ],
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
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduleVisitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('doctor list length should be 1', () => {
    component.getAllDoctors();
    expect(component.doctorList.length).toEqual(1);
  });
  it('shuold be correct day of week', ()=> {
    expect(component.translateDayOfWeek('TUESDAY')).toBe('Tuesday');
  });
  it('should be correct value for dayFrom and dayTo', () => {
    component.dayFrom = 0;
    component.dayTo = 0;
    component.goToNextWeek()
    component.goToNextWeek()
    expect(component.dayFrom).toEqual(14);
    expect(component.dayTo).toEqual(14);
    component.goToPreviousWeek();
    expect(component.dayFrom).toEqual(7);
    expect(component.dayTo).toEqual(7);
  });
  it('shuold be correct state on scheduleVisit', ()=>{
    component.selectedIndex = 0;
    component.visitDateTime = null;
    component.scheduleVisit();
    expect(component.answer.serverAnswer).toEqual('VISIT_TERM_EMPTY');
    component.visitDateTime = '2021-02-01;10 30';
  });
  it('shuold be correct state on onCorrectScheduled', ()=>{
    component.selectedIndex = 0;
    component.onCorrectScheduled();
    expect(component.patientAilmentsInfo ).toBeNull();
    expect(component.answer.serverAnswer ).toBeNull();
    expect(component.doctorList.length).toEqual(1);
  });
});
