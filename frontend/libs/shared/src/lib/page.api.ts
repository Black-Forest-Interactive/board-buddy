export interface GenericRequestResult {
  success: boolean,
  msg: string
}

export interface Page<T> {
  content: T[];
  pageable: Pageable;
  totalPages: number;
  totalSize: number;
  offset: number;
  size: number;
  empty: boolean;
}

export interface Pageable {
  offset: number;
  number: number;
  size: number;
}

export class PagingSettings {
  constructor(
    public pageNumber: number = 0,
    public totalElements: number = 0,
    public pageSize: number = 20
  ) {
  }
}
