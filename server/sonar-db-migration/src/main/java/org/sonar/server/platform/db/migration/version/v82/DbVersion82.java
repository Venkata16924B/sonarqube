/*
 * SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v82;

import org.sonar.server.platform.db.migration.step.MigrationStepRegistry;
import org.sonar.server.platform.db.migration.version.DbVersion;

public class DbVersion82 implements DbVersion {
  @Override
  public void addSteps(MigrationStepRegistry registry) {
    registry
      .add(3200, "Drop 'In Review' Security Hotspots status ", DropSecurityHotSpotsInReviewStatus.class)
      .add(3201, "Migrate Manual Vulnerabilities to Security Hotspots ", MigrateManualVulnerabilitiesToSecurityHotSpots.class)
      .add(3202, "Remove 'newsbox.dismiss.hotspots' user property", RemoveNewsboxDismissHotspotsProperty.class)
      .add(3203, "Ensure Security Hotspots have status TO_REVIEW", EnsureHotspotDefaultStatusIsToReview.class);
  }
}
