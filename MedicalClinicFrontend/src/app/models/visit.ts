import { VisitStatus } from 'src/app/enums/visit-status.enum';
import { Doctor } from './doctor';
import { Employee } from './employee';
import { Patient } from './patient';
import { Diagnosis } from 'src/app/models/diagnosis';

export class Visit {

id: number;
visitDateTime: Date;
visitStatus: VisitStatus;
visitStatusString: string;
patient: Patient;
doctor: Doctor;
employee: Employee;
patientAilmentsInfo: string;
diagnosis: Diagnosis;
reservationDateTime: Date;

  constructor() { }
}
