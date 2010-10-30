/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.forge.shell.plugins.builtin;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.forge.project.Resource;
import org.jboss.seam.forge.project.services.ResourceFactory;
import org.jboss.seam.forge.project.util.ResourceUtil;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.Plugin;
import org.jboss.seam.forge.shell.util.GeneralUtils;

import static org.jboss.seam.forge.project.util.ResourceUtil.parsePathspec;
import static org.jboss.seam.forge.shell.util.GeneralUtils.printOutColumns;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
@Named("ls")
@Help("Prints the contents current directory.")
public class LsPlugin implements Plugin
{
   private final Shell shell;
   private final ResourceFactory factory;

   @Inject
   public LsPlugin(final Shell shell, final ResourceFactory factory)
   {
      this.shell = shell;
      this.factory = factory;
   }

   @DefaultCommand
   public void run(@Option(flagOnly = true, name = "all", shortName = "a", required = false) final boolean showAll,
                   @Option(flagOnly = true, name = "list", shortName = "l", required = false) final boolean list,
                   @Option(description = "path", defaultValue = ".") String... path)
   {
      List<String> listData = new LinkedList<String>();

      for (String p : path)
      {
         Resource<?> resource = parsePathspec(factory, shell.getCurrentResource(), p);

         List<Resource<?>> childResources = resource.listResources();


         String el;
         for (Resource<?> r : childResources)
         {
            el = r.toString();

            if (showAll || !el.startsWith("."))
            {
               listData.add(el);
            }
         }
      }

      if (list) System.out.println("LIST!");

      printOutColumns(listData, shell, true);
   }
}
