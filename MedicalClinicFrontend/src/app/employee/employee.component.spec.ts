import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../services/language.service';
import { UserService } from '../services/user.service';
import { EmployeeComponent } from './employee.component';

describe('EmployeeComponent', () => {
  let component: EmployeeComponent;
  let fixture: ComponentFixture<EmployeeComponent>;
  let router: Router;
  let location: Location;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      declarations: [ EmployeeComponent ],
      providers: [TranslateService, HttpClient, LanguageService, UserService,
        { provide: APP_BASE_HREF, useValue : '/' }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(EmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shhould be navigate to /schedulevisit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#schedulevisit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/schedulevisit');
  }));
  it('shhould be navigate to /patientvisits page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#patientvisits'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/patientvisits');
  }));
  it('shhould be navigate to /visitconfirmation page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#visitconfirmation'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/visitconfirmation');
  }));
  it('shhould be navigate to /cancelvisit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#cancelvisit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/cancelvisit');
  }));
  it('shhould be navigate to /addpatient page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#addpatient'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/addpatient');
  }));
  it('shhould be navigate to /selfedit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#selfedit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfedit');
  }));
});
