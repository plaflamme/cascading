/*
 * Copyright (c) 2007-2010 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package cascading.operation.regex;

import java.beans.ConstructorProperties;
import java.util.regex.Pattern;

import cascading.flow.FlowProcess;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;

/**
 * Class RegexGenerator will emit a new Tuple for every split on the incoming argument value delimited by the given patternString.
 * <p/>
 * This could be used to break a document into single word tuples for later processing for a word count.
 */
public class RegexSplitGenerator extends RegexOperation<Pattern> implements Function<Pattern>
  {
  /**
   * Constructor RegexGenerator creates a new RegexGenerator instance.
   *
   * @param patternString of type String
   */
  @ConstructorProperties({"patternString"})
  public RegexSplitGenerator( String patternString )
    {
    super( 1, Fields.size( 1 ), patternString );
    }

  /**
   * Constructor RegexGenerator creates a new RegexGenerator instance.
   *
   * @param fieldDeclaration of type Fields
   * @param patternString    of type String
   */
  @ConstructorProperties({"fieldDeclaration", "patternString"})
  public RegexSplitGenerator( Fields fieldDeclaration, String patternString )
    {
    super( 1, fieldDeclaration, patternString );

    if( fieldDeclaration.size() != 1 )
      throw new IllegalArgumentException( "fieldDeclaration may only declare one field, was " + fieldDeclaration.print() );
    }

  @Override
  public void prepare( FlowProcess flowProcess, OperationCall<Pattern> operationCall )
    {
    operationCall.setContext( getPattern() );
    }

  /** @see cascading.operation.Function#operate(cascading.flow.FlowProcess,cascading.operation.FunctionCall) */
  public void operate( FlowProcess flowProcess, FunctionCall<Pattern> functionCall )
    {
    String value = functionCall.getArguments().getString( 0 );

    if( value == null )
      value = "";

    String[] split = functionCall.getContext().split( value );

    for( String string : split )
      functionCall.getOutputCollector().add( new Tuple( string ) );
    }
  }