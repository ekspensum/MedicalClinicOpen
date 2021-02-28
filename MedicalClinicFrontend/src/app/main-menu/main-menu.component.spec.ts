import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { routes } from '../app-routing.module';
import { MockAuthenticationService } from '../mocks/mock-authentication.service';
import { AuthenticationService } from '../services/authentication.service';
import { MainMenuComponent } from './main-menu.component';

describe('MainMenuComponent', () => {
  let component: MainMenuComponent;
  let fixture: ComponentFixture<MainMenuComponent>;
  let router: Router;
  let location: Location;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MainMenuComponent ],
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
      providers: [TranslateService, HttpClient,
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: AuthenticationService, useClass: MockAuthenticationService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(MainMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shhould be navigate to /owner page', fakeAsync(() => {
    const ownerLink = fixture.debugElement.query(By.css('#owner'));
    ownerLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/owner');
  }));
  it('shhould be navigate to /admin page', fakeAsync(() => {
    const adminLink = fixture.debugElement.query(By.css('#admin'));
    adminLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/admin');
  }));
  it('shhould be navigate to /employee page', fakeAsync(() => {
    const employeeLink = fixture.debugElement.query(By.css('#employee'));
    employeeLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/employee');
  }));
  it('shhould be navigate to /doctor page', fakeAsync(() => {
    const doctorLink = fixture.debugElement.query(By.css('#doctor'));
    doctorLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/doctor');
  }));
  it('shhould be navigate to /patient page', fakeAsync(() => {
    const patientLink = fixture.debugElement.query(By.css('#patient'));
    patientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/patient');
  }));
  it('shhould be navigate to /agenda page', fakeAsync(() => {
    const agendaLink = fixture.debugElement.query(By.css('#agenda'));
    agendaLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/agenda');
  }));
  it('shhould be navigate to /contactus page', fakeAsync(() => {
    const contactusLink = fixture.debugElement.query(By.css('#contactus'));
    contactusLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/contactus');
  }));
  it('shhould be navigate to /login page', fakeAsync(() => {
    const loginLink = fixture.debugElement.query(By.css('#login'));
    loginLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/login');
  }));
});
