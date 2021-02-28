import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { UserService } from '../services/user.service';
import { DoctorComponent } from './doctor.component';
import { By } from '@angular/platform-browser';

describe('DoctorComponent', () => {
  let component: DoctorComponent;
  let fixture: ComponentFixture<DoctorComponent>;
  let router: Router;
  let location: Location;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DoctorComponent],
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
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue : '/' }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(DoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shhould be navigate to /myvisits page', fakeAsync(() => {
    const myVisitLink = fixture.debugElement.query(By.css('#myvisits'));
    myVisitLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/myvisits');
  }));
  it('shhould be navigate to /makediagnosis page', fakeAsync(() => {
    const makediagnosisLink = fixture.debugElement.query(By.css('#makediagnosis'));
    makediagnosisLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/makediagnosis');
  }));
  it('shhould be navigate to /selfeditdata page', fakeAsync(() => {
    const selfeditdataLink = fixture.debugElement.query(By.css('#selfeditdata'));
    selfeditdataLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfeditdata');
  }));
  it('shhould be navigate to /selfeditagenda page', fakeAsync(() => {
    const selfeditagendaLink = fixture.debugElement.query(By.css('#selfeditagenda'));
    selfeditagendaLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfeditagenda');
  }));
  it('shhould be navigate to /selfsetfreedays page', fakeAsync(() => {
    const selfeditagendaLink = fixture.debugElement.query(By.css('#selfsetfreedays'));
    selfeditagendaLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfsetfreedays');
  }));
});
