import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { routes } from 'src/app/app-routing.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';
import { MyVisitsComponent } from './my-visits.component';
import { Router, RoutesRecognized } from '@angular/router';

describe('MyVisitsComponent', () => {
  let component: MyVisitsComponent;
  let fixture: ComponentFixture<MyVisitsComponent>;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MyVisitsComponent ],
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
        { provide: VisitService, useClass: MockVisitService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    router.initialNavigation();
    fixture = TestBed.createComponent(MyVisitsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on searchPatientVisits', ()=>{
    component.searchPatientVisits();
    expect(component.visitList.length).toEqual(0);
  });
  it('should be navigate to /patient/visitdetails page', () => {
    component.selectVisit();
    router.events.subscribe((url: any) => {
      if (url instanceof RoutesRecognized) {
        expect(url.url).toEqual('/patient/visitdetails');
      }
    });
  });
});
