import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { MedicalDocument } from 'src/app/models/medical-document';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

import { MedicalDocumentationComponent } from './medical-documentation.component';

describe('MedicalDocumentationComponent', () => {
  let component: MedicalDocumentationComponent;
  let fixture: ComponentFixture<MedicalDocumentationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MedicalDocumentationComponent ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
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
    fixture = TestBed.createComponent(MedicalDocumentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on ngOnInit', ()=>{
    component.ngOnInit();
    expect(component.medicalDocumentList.length).toEqual(1);
    expect(component.medicalDocumentList[0].enabled).toBeTrue();
  });
  it('tracks that function was called and tracks the argument of its call', () => {
    let eventFile = new Event('input');
    spyOn(component, 'onFileSelect');
    component.onFileSelect(eventFile);
    expect(component.onFileSelect).toHaveBeenCalled();
    expect(component.onFileSelect).toHaveBeenCalledWith(eventFile);
  });
  it('shuold be correct state on onRemoveFile', ()=>{
    spyOn(window, 'confirm').and.returnValue(true);
    component.onRemoveFile(1);
    expect(component.answer.serverAnswer).toEqual('UPDATE_MEDICAL_DOCUMENTATION');
  });
  it('shuold be correct state on updateMedicalDocumentation', ()=>{
    component.updateMedicalDocumentation();
    expect(component.answer.serverAnswer).toEqual('UPDATE_MEDICAL_DOCUMENTATION');
    expect(component.medicalDocListSaved).toBeTrue();
  });
  it('shuold be correct state on updateMedicalDocumentation', ()=>{
    spyOn(window, 'open');
    component.onDisplayFile(new MedicalDocument());
    expect(window.open).toHaveBeenCalled()
  });
});
