import { APP_BASE_HREF} from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RoutesRecognized } from '@angular/router';
import { routes } from 'src/app/app-routing.module';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';
import { DoctorVisitsComponent } from './doctor-visits.component';

describe('DoctorVisitsComponent', () => {
  let component: DoctorVisitsComponent;
  let fixture: ComponentFixture<DoctorVisitsComponent>;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DoctorVisitsComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        FormsModule,
        ReactiveFormsModule,
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
    fixture = TestBed.createComponent(DoctorVisitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('Doctor planned visits list length should be 2', () => {
    let dateFromSearch = new Date().toISOString().slice(0, 10)
    let dateToSearch = new Date().toISOString().slice(0, 10)
    component.dateRangeForm = new FormGroup({
      dateFrom: new FormControl(dateFromSearch),
      dateTo: new FormControl(dateToSearch)
    });
    component.searchDoctorVisits();
    expect(component.visitList.length).toEqual(2);
  });
  it('should be navigate to /doctor/visitdetails page', () => {
    component.selectVisit();
    router.events.subscribe((url: any) => {
      if (url instanceof RoutesRecognized) {
        expect(url.url).toEqual('/doctor/visitdetails');
      }
    });
  });
});
