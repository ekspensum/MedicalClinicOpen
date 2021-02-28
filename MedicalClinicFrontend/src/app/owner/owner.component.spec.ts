import { APP_BASE_HREF, Location } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { routes } from '../app-routing.module';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { UserService } from '../services/user.service';
import { OwnerComponent } from './owner.component';
import { By } from '@angular/platform-browser';

describe('OwnerComponent', () => {
  let component: OwnerComponent;
  let fixture: ComponentFixture<OwnerComponent>;
  let router: Router;
  let location: Location;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OwnerComponent ],
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
        { provide: APP_BASE_HREF, useValue : '/' }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
    fixture = TestBed.createComponent(OwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shhould be navigate to /addadmin page', fakeAsync(() => {
    const addadminLink = fixture.debugElement.query(By.css('#addadmin'));
    addadminLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/addadmin');
  }));
  it('shhould be navigate to /editadmin page', fakeAsync(() => {
    const editadminLink = fixture.debugElement.query(By.css('#editadmin'));
    editadminLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/editadmin');
  }));
  it('shhould be navigate to /selfedit page', fakeAsync(() => {
    const selfeditLink = fixture.debugElement.query(By.css('#selfedit'));
    selfeditLink.triggerEventHandler('click', {button: 0});
    tick();
    fixture.detectChanges();
    expect(location.path()).toEqual('/selfedit');
  }));
});
