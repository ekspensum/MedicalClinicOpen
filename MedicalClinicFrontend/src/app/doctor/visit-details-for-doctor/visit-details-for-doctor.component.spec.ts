import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { VisitService } from 'src/app/services/visit.service';
import { VisitDetailsForDoctorComponent } from './visit-details-for-doctor.component';

describe('VisitDetailsForDoctorComponent', () => {
  let component: VisitDetailsForDoctorComponent;
  let fixture: ComponentFixture<VisitDetailsForDoctorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitDetailsForDoctorComponent ],
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
        { provide: VisitService, useClass: MockVisitService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitDetailsForDoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on ngOnInit', () => {
    component.ngOnInit();
    expect(component.enabledMedicalDocList.length).toEqual(1);
  });
});
