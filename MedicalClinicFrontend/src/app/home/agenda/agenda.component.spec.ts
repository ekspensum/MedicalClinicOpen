import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { AgendaComponent } from './agenda.component';

describe('AgendaComponent', () => {
  let component: AgendaComponent;
  let fixture: ComponentFixture<AgendaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AgendaComponent],
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
    fixture = TestBed.createComponent(AgendaComponent);
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
    expect(component.translateDayOfWeek('WEDNESDAY')).toBe('Wednesday');
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
});
