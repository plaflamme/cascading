/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
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

package cascading.flow.local;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.FlowException;
import cascading.flow.FlowProcess;
import cascading.flow.planner.ElementGraph;
import cascading.flow.planner.StepGraph;

/**
 *
 */
public class LocalFlow extends Flow<Properties>
  {
  private Properties config;
  private Thread shutdownHook;

  public LocalFlow( Map<Object, Object> properties, Properties config, FlowDef flowDef, ElementGraph elementGraph, StepGraph stepGraph )
    {
    super( properties, config, flowDef, elementGraph, stepGraph );
    }

  @Override
  protected void initConfig( Map<Object, Object> properties, Properties parentConfig )
    {
    this.config = createConfig( properties, parentConfig );
    }

  @Override
  protected void setConfigProperty( Properties properties, Object key, Object value )
    {
    properties.setProperty( key.toString(), value.toString() );
    }

  @Override
  protected Properties newConfig( Properties defaultConfig )
    {
    return defaultConfig == null ? new Properties() : new Properties( defaultConfig );
    }

  @Override
  public Properties getConfig()
    {
    return config;
    }

  @Override
  public Properties getConfigCopy()
    {
    return new Properties( config );
    }

  @Override
  public Map<Object, Object> getConfigAsProperties()
    {
    return config;
    }

  @Override
  public String getProperty( String key )
    {
    return config.getProperty( key );
    }

  @Override
  protected void initFromProperties( Map<Object, Object> properties )
    {
    super.initFromProperties( properties );
    }

  @Override
  public FlowProcess getFlowProcess()
    {
    return new LocalFlowProcess( getFlowSession(), config );
    }

  @Override
  protected void internalStart()
    {
    try
      {
      deleteSinksIfReplace();
      }
    catch( IOException exception )
      {
      throw new FlowException( "unable to delete sinks", exception );
      }

    registerShutdownHook();
    }

  private void registerShutdownHook()
    {
    if( !isStopJobsOnExit() )
      return;

    shutdownHook = new Thread()
    {
    @Override
    public void run()
      {
      LocalFlow.this.stop();
      }
    };

    Runtime.getRuntime().addShutdownHook( shutdownHook );
    }

  @Override
  protected void internalClean( boolean force )
    {
    }

  @Override
  public boolean stepsAreLocal()
    {
    return false;
    }

  @Override
  protected int getMaxNumParallelSteps()
    {
    return 0;
    }

  @Override
  protected void internalShutdown()
    {
    }
  }
