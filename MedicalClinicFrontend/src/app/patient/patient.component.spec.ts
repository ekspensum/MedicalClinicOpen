import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../services/language.service';
import { PatientComponent } from './patient.component';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';

describe('PatientComponent', () => {
  let component: PatientComponent;
  let fixture: ComponentFixture<PatientComponent>;
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
      declarations: [PatientComponent],
      providers: [TranslateService, HttpClient, LanguageService,
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(PatientComponent);
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
  it('shhould be navigate to /myvisits page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#myvisits'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/myvisits');
  }));
  it('shhould be navigate to /cancelvisit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#cancelvisit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/cancelvisit');
  }));
  it('shhould be navigate to /medicaldocumentation page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#medicaldocumentation'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/medicaldocumentation');
  }));
  it('shhould be navigate to /selfedit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#selfedit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfedit');
  }));
  it('shhould be navigate to /removemyaccount page', fakeAsync(() => {
    const removemyaccountLink = fixture.debugElement.query(By.css('#removemyaccount'));
    removemyaccountLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/removemyaccount');
  }));
});
