import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { UserService } from '../services/user.service';
import { AdminComponent } from './admin.component';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;
  let router: Router;
  let location: Location;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminComponent ],
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
      providers: [TranslateService, HttpClient, UserService,
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shhould be navigate to /addemployee page', fakeAsync(() => {
    const addemployeeLink = fixture.debugElement.query(By.css('#addemployee'));
    addemployeeLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/addemployee');
  }));
  it('shhould be navigate to /editemployee page', fakeAsync(() => {
    const editemployeeLink = fixture.debugElement.query(By.css('#editemployee'));
    editemployeeLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/editemployee');
  }));
  it('shhould be navigate to /adddoctor page', fakeAsync(() => {
    const adddoctorLink = fixture.debugElement.query(By.css('#adddoctor'));
    adddoctorLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/adddoctor');
  }));
  it('shhould be navigate to /editdoctor page', fakeAsync(() => {
    const editdoctorLink = fixture.debugElement.query(By.css('#editdoctor'));
    editdoctorLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/editdoctor');
  }));
  it('shhould be navigate to /addpatient page', fakeAsync(() => {
    const addpatientLink = fixture.debugElement.query(By.css('#addpatient'));
    addpatientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/addpatient');
  }));
  it('shhould be navigate to /editpatient page', fakeAsync(() => {
    const editpatientLink = fixture.debugElement.query(By.css('#editpatient'));
    editpatientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/editpatient');
  }));
  it('shhould be navigate to /removepatient page', fakeAsync(() => {
    const removepatientLink = fixture.debugElement.query(By.css('#removepatient'));
    removepatientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/removepatient');
  }));
  it('shhould be navigate to /doctorfreedays page', fakeAsync(() => {
    const removepatientLink = fixture.debugElement.query(By.css('#doctorfreedays'));
    removepatientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/doctorfreedays');
  }));
  it('shhould be navigate to /clinicfreedays page', fakeAsync(() => {
    const removepatientLink = fixture.debugElement.query(By.css('#clinicfreedays'));
    removepatientLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/clinicfreedays');
  }));
  it('shhould be navigate to /selfedit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#selfedit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfedit');
  }));
});
