grammar FiltersQuery;
import VariableLexer;

// TODO: support in range filter

filtersQuery:
    WS? (associate WS)? filterBy sortBy? offset? limit? WS? EOF
    ;

filterBy:
    FILTER WS BY WS filters
    ;

// filters

filters:
    filter opFilter*
    | wrappedFilters opFilter*
    | L_PAREN WS? R_PAREN
    ;

wrappedFilters:
    L_PAREN WS? filters WS? R_PAREN
    ;

opFilter:
    boolOperator (filter | wrappedFilters)
    ;

filter:
    betweenCriteria
    | emptyCriteria
    | notEmptyCriteria
    | equalCriteria
    | notEqualCriteria
    | startsWithCriteria
    | endsWithCriteria
    | likeCriteria
    | greaterThanCriteria
    | greaterEqualThanCriteria
    | lessThanCriteria
    | lessEqualThanCriteria
    | inCriteria
    ;

boolOperator:
    WS (LOGICAL_OR
    | LOGICAL_AND) WS
    ;

betweenCriteria: propertyName WS BETWEEN WS betweenCriteriaValue;

betweenCriteriaValue:
  L_BRACKET WS? (
  (propertyValue WS? COMMA WS? propertyValue?)
  | (COMMA WS? propertyValue)
  ) WS? R_BRACKET;

emptyCriteria: propertyName WS? EQUALS WS? NIL_LIT;

notEmptyCriteria: propertyName WS? NOT_EQUALS WS? NIL_LIT;

startsWithCriteria: propertyName WS STARTSWITH WS stringPropertyValue;

endsWithCriteria: propertyName WS ENDSWITH WS stringPropertyValue;

likeCriteria: propertyName WS LIKE WS stringPropertyValue;

equalCriteria: propertyName WS? EQUALS WS? propertyValueWithReferenceBoolNull;

notEqualCriteria: propertyName WS? NOT_EQUALS WS? propertyValueWithReferenceBoolNull;

greaterThanCriteria: propertyName WS? GREATER WS? propertyValueWithReference;

greaterEqualThanCriteria: propertyName WS? GREATER_OR_EQUALS WS? propertyValueWithReference;

lessThanCriteria: propertyName WS? LESS WS? propertyValueWithReference;

lessEqualThanCriteria: propertyName WS? LESS_OR_EQUALS WS? propertyValueWithReference;

inCriteria: propertyName WS? IN WS? propertyValueList;

// others

associate:
    ASSOCIATE WS associateTargets
    ;

associateTargets:
    associateTarget (WS? COMMA WS? associateTarget)*
    ;

sortBy:
    WS SORT WS BY WS sorts
    ;

sorts:
    order WS? (COMMA WS? order)*
    ;

order:
    propertyName WS (ASC | DESC)
    ;

offset:
    WS OFFSET WS decimal
    ;

limit:
    WS LIMIT WS decimal
    ;

entityName:
    IDENTIFIER
    ;

entityAliasName:
    IDENTIFIER
    ;

associateTarget:
    IDENTIFIER
    ;

propertyName:
    IDENTIFIER (DOT IDENTIFIER)?
    ;

propertyValueList:
    L_BRACKET WS? propertyValueWithReferenceBoolNull (WS? COMMA WS? propertyValueWithReferenceBoolNull)* WS? R_BRACKET
    ;

propertyValue:
    ZERO
    | DECIMAL_LIT
    | FLOAT_LIT
    | INTERPRETED_STRING_LIT
    ;

propertyValueWithReference:
    propertyValue
    | propertyName
    ;

propertyValueWithReferenceBoolNull:
    propertyValue
    | propertyName
    | TRUE
    | FALSE
    | NULL
    ;

decimal:
    ZERO
    | DECIMAL_LIT
    ;

stringPropertyValue:
    INTERPRETED_STRING_LIT
    ;
