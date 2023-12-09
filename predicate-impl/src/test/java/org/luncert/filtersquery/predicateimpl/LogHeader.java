package org.luncert.filtersquery.predicateimpl;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogHeader implements Serializable {

  @Serial
  private static final long serialVersionUID = 3413266763239477699L;

  private long id;

  private String externalReference;

  private String severity;

  private String categoryId;

  private String subCategoryId;

  private long createdAt;
}
