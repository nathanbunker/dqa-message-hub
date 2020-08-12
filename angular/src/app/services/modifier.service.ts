import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {concat, interval, Observable} from 'rxjs';
import {flatMap, pluck, takeWhile} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ModifierService {

  constructor(private $http: HttpClient) {}

  uploadFileToUrl(file: File): Observable<FileUploadInfo> {
    const form: FormData = new FormData();
    form.append('file', file);
    return this.$http.post<FileUploadInfo>('api/modify/upload-messages', form);
  }

  initiateFileProcess(fileId: string): Observable<FileUploadInfo> {
    const form: FormData = new FormData();
    form.append('fileId', fileId);
    return this.$http.post<FileUploadInfo>('api/modify/process-file', form);
  }

  watch(fileId: string): Observable<FileUploadInfo> {
    return concat(interval(1000).pipe(
      flatMap(tick => {
        return this.reportFileProcess(fileId);
      }),
      takeWhile((info: FileUploadInfo) => (info.status === 'started' || info.status === 'reading')),
    ), this.reportFileProcess(fileId));
  }

  reportFileProcess(fileId: string): Observable<FileUploadInfo> {
    return this.$http.get<FileUploadInfo>('api/modify/report-file', {
      params: {
        fileId: fileId
      }
    });
  }

  getQueues(): Observable<FileUploadInfo[]> {
    return this.$http.get('api/modify/get-queues').pipe(
      pluck('uploads')
    );
  }

  pauseFileProcess(fileId: string) {
    return this.$http.get<FileUploadInfo>('api/modify/stop-file', {
      params: {
        fileId: fileId
      }
    });
  }

  unpauseFileProcess(fileId: string) {
    return this.$http.get('api/modify/unpause-file', {
      params: {
        fileId: fileId
      }
    });
  }

  getAcks(fileId: string) {
    return this.$http.get<string[]>('api/modify/report-acks', {
      params: {
        fileId: fileId
      }
    });
  }

  removeFile(fileId: string) {
    return this.$http.get('api/modify/remove-file', {
      params: {
        fileId: fileId
      }
    });
  }
}


export interface FileUploadInfo {
  fileId: string;
  status: string;
  fileName: string;
  numberOfMessages: number;
  numberProcessed: number;
  averageElapsed: number;
  elapsedTimeMs: number;
  numberUnProcessed: number;
  percentage: number;
  deleteRequested: boolean;
  countdown: number;
}
