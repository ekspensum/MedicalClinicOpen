import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { MockVisitService } from 'src/app/mocks/mock-visit.service';
import { UserService } from 'src/app/services/user.service';
import { VisitService } from 'src/app/services/visit.service';

import { CancelVisitComponent } from './cancel-visit.component';

describe('CancelVisitComponent', () => {
  let component: CancelVisitComponent;
  let fixture: ComponentFixture<CancelVisitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CancelVisitComponent ],
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
        { provide: VisitService, useClass: MockVisitService },
        { provide: UserService, useClass: MockUserService }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelVisitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on ngOnInit', ()=>{
    component.ngOnInit();
    expect(component.visitList.length).toEqual(0);
  });
  it('shuold be correct state on cancelVisit', ()=>{
    spyOn(window, 'confirm').and.returnValue(true);
    component.cancelVisit();
    expect(component.answer.serverAnswer).toEqual('DELETED_VISIT');
  });
  it('shuold be correct state on onCanceledVisit', ()=>{
    component.onCanceledVisit();
    expect(component.answer.serverAnswer).toBeNull();
  });
});
