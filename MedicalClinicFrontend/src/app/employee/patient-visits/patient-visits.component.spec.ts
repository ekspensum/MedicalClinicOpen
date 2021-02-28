import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { routes } from 'src/app/app-routing.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { Patient } from 'src/app/models/patient';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { Router, RoutesRecognized } from '@angular/router';
import { PatientVisitsComponent } from './patient-visits.component';

describe('PatientVisitsComponent', () => {
  let component: PatientVisitsComponent;
  let fixture: ComponentFixture<PatientVisitsComponent>;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PatientVisitsComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
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
    router = TestBed.inject(Router);
    router.initialNavigation();
    fixture = TestBed.createComponent(PatientVisitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('patient list length should be 1', () => {
    component.searchPatient();
    expect(component.patientList.length).toEqual(1);
  });
  it('shuold be correct state on selectPatient', ()=>{
    component.selectedIndexPatient = 0;
    component.selectPatient();
    expect(component.patientList.length).toEqual(0);
    expect(component.selectedIndexPatient).toBeNull();
    expect(component.patient).not.toBeNull();
  });
  it('shuold be correct state on searchVisit', ()=>{
    component.patient = new Patient(new User());
    component.searchVisit();
    expect(component.visitList.length).toEqual(0);
  });
  it('should be navigate to /employee/visitdetails page', () => {
    component.selectVisit();
    router.events.subscribe((url: any) => {
      if (url instanceof RoutesRecognized) {
        expect(url.url).toEqual('/employee/visitdetails');
      }
    });
  });
});
