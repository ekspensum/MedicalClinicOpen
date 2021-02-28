import { MedicalByteFile } from "./medical-byte-file";

export class MedicalDocument {

  id: number;
  enabled: boolean;
  medicalByteFile: MedicalByteFile;
  description: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  registerDateTime: Date;

  constructor() { }
}
