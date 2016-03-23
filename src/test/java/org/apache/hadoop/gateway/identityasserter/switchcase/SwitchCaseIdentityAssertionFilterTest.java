/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.identityasserter.switchcase;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.servlet.FilterConfig;

import org.apache.hadoop.gateway.security.GroupPrincipal;
import org.apache.hadoop.gateway.security.PrimaryPrincipal;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;

public class SwitchCaseIdentityAssertionFilterTest {

  @Test
  public void testDefaultConfig() throws Exception {
    FilterConfig config = EasyMock.createNiceMock( FilterConfig.class );
    EasyMock.replay( config );

    SwitchCaseIdentityAssertionFilter filter = new SwitchCaseIdentityAssertionFilter();

    Subject subject = new Subject();
    subject.getPrincipals().add(new PrimaryPrincipal( "Member@us.apache.org" ) );
    subject.getPrincipals().add(new GroupPrincipal( "user" ) );
    subject.getPrincipals().add( new GroupPrincipal( "Admin" ) );

    // First test is with no config.  Since the output template is the empty string that should be the result.
    filter.init(config);
    String actual = filter.mapUserPrincipal(((Principal) subject.getPrincipals(PrimaryPrincipal.class).toArray()[0]).getName());
    String[] groups = filter.mapGroupPrincipals(actual, subject);
    assertThat( actual, is( "member@us.apache.org" ) );
    assertThat( groups, is( arrayContainingInAnyOrder( "admin", "user" ) ) );

  }

  @Test
  public void testUpperPrincipalAndGroups() throws Exception {
    FilterConfig config = EasyMock.createNiceMock( FilterConfig.class );
    EasyMock.expect( config.getInitParameter( "user" ) ).andReturn( "Upper" ).anyTimes();
    EasyMock.expect( config.getInitParameter( "group" ) ).andReturn( "Upper" ).anyTimes();
    EasyMock.replay( config );

    SwitchCaseIdentityAssertionFilter filter = new SwitchCaseIdentityAssertionFilter();

    Subject subject = new Subject();
    subject.getPrincipals().add(new PrimaryPrincipal( "Member@us.apache.org" ) );
    subject.getPrincipals().add(new GroupPrincipal( "user" ) );
    subject.getPrincipals().add( new GroupPrincipal( "Admin" ) );

    filter.init(config);
    String actual = filter.mapUserPrincipal(((Principal) subject.getPrincipals(PrimaryPrincipal.class).toArray()[0]).getName());
    String[] groups = filter.mapGroupPrincipals(actual, subject);
    assertThat( actual, is( "MEMBER@US.APACHE.ORG" ) );
    assertThat( groups, is( arrayContainingInAnyOrder( "ADMIN", "USER" ) ) );

  }

  @Test
  public void testLowerPrincipalAndGroups() throws Exception {
    FilterConfig config = EasyMock.createNiceMock( FilterConfig.class );
    EasyMock.expect( config.getInitParameter( "user" ) ).andReturn( "lower" ).anyTimes();
    EasyMock.expect( config.getInitParameter( "group" ) ).andReturn( "LOWER" ).anyTimes();
    EasyMock.replay( config );

    SwitchCaseIdentityAssertionFilter filter = new SwitchCaseIdentityAssertionFilter();

    Subject subject = new Subject();
    subject.getPrincipals().add(new PrimaryPrincipal( "Member@us.apache.org" ) );
    subject.getPrincipals().add(new GroupPrincipal( "user" ) );
    subject.getPrincipals().add( new GroupPrincipal( "Admin" ) );

    filter.init(config);
    String actual = filter.mapUserPrincipal(((Principal) subject.getPrincipals(PrimaryPrincipal.class).toArray()[0]).getName());
    String[] groups = filter.mapGroupPrincipals(actual, subject);
    assertThat( actual, is( "member@us.apache.org" ) );
    assertThat( groups, is( arrayContainingInAnyOrder( "admin", "user" ) ) );

  }

  @Test
  public void testNonePrincipalAndGroups() throws Exception {
    FilterConfig config = EasyMock.createNiceMock( FilterConfig.class );
    EasyMock.expect( config.getInitParameter( "user" ) ).andReturn( "none" ).anyTimes();
    EasyMock.expect( config.getInitParameter( "group" ) ).andReturn( "NONE" ).anyTimes();
    EasyMock.replay( config );

    SwitchCaseIdentityAssertionFilter filter = new SwitchCaseIdentityAssertionFilter();

    Subject subject = new Subject();
    subject.getPrincipals().add(new PrimaryPrincipal( "Member@us.apache.org" ) );

    filter.init(config);
    String actual = filter.mapUserPrincipal(((Principal) subject.getPrincipals(PrimaryPrincipal.class).toArray()[0]).getName());
    String[] groups = filter.mapGroupPrincipals(actual, subject);
    assertThat( actual, is( "Member@us.apache.org" ) );
    assertThat( groups, is( nullValue() ) );

  }

  @Test
  public void testNoGroups() throws Exception {
    FilterConfig config = EasyMock.createNiceMock( FilterConfig.class );
    EasyMock.expect( config.getInitParameter( "user" ) ).andReturn( "upper" ).anyTimes();
    EasyMock.expect( config.getInitParameter( "group" ) ).andReturn( "upper" ).anyTimes();
    EasyMock.replay( config );

    SwitchCaseIdentityAssertionFilter filter = new SwitchCaseIdentityAssertionFilter();

    Subject subject = new Subject();
    subject.getPrincipals().add(new PrimaryPrincipal( "Member@us.apache.org" ) );

    filter.init(config);
    String actual = filter.mapUserPrincipal(((Principal) subject.getPrincipals(PrimaryPrincipal.class).toArray()[0]).getName());
    String[] groups = filter.mapGroupPrincipals(actual, subject);
    assertThat( actual, is( "MEMBER@US.APACHE.ORG" ) );
    assertThat( groups, is( nullValue() ) );

  }

}
