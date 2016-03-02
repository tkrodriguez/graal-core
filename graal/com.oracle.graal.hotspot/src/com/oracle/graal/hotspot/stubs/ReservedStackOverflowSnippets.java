/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.hotspot.stubs;

import static com.oracle.graal.hotspot.HotSpotHostBackend.ENABLE_STACK_RESERVED_ZONE;
import static com.oracle.graal.hotspot.HotSpotHostBackend.THROW_DELAYED_STACKOVERFLOW_ERROR;
import static com.oracle.graal.hotspot.nodes.TailJumpToHandlerNode.tailJumpToHandler;
import static com.oracle.graal.hotspot.replacements.HotSpotReplacementsUtil.registerAsWord;
import static com.oracle.graal.replacements.SnippetTemplate.DEFAULT_REPLACER;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.hotspot.HotSpotVMConfig;

import com.oracle.graal.compiler.common.spi.ForeignCallDescriptor;
import com.oracle.graal.graph.Node.ConstantNodeParameter;
import com.oracle.graal.graph.Node.NodeIntrinsic;
import com.oracle.graal.hotspot.meta.HotSpotHostForeignCallsProvider;
import com.oracle.graal.hotspot.meta.HotSpotProviders;
import com.oracle.graal.hotspot.meta.HotSpotRegistersProvider;
import com.oracle.graal.hotspot.nodes.ReservedStackAccessCheckNode;
import com.oracle.graal.nodes.extended.ForeignCallNode;
import com.oracle.graal.nodes.spi.LoweringTool;
import com.oracle.graal.replacements.Snippet;
import com.oracle.graal.replacements.Snippet.ConstantParameter;
import com.oracle.graal.replacements.SnippetTemplate.AbstractTemplates;
import com.oracle.graal.replacements.SnippetTemplate.Arguments;
import com.oracle.graal.replacements.SnippetTemplate.SnippetInfo;
import com.oracle.graal.replacements.Snippets;
import com.oracle.graal.word.Word;

public class ReservedStackOverflowSnippets implements Snippets {

    @Snippet
    public static void checkReservedAndThrow(
                    @ConstantParameter Register threadRegister, @ConstantParameter Register sp, @ConstantParameter int javaThreadReservedStackActivationOffset) {
        Word thread = registerAsWord(threadRegister);
        Word reservedStack = thread.readWord(javaThreadReservedStackActivationOffset);
        Word theSp = registerAsWord(sp);
        if (reservedStack.belowThan(theSp)) {
            enableReservedZone(ENABLE_STACK_RESERVED_ZONE, thread);
            tailJumpToHandler(THROW_DELAYED_STACKOVERFLOW_ERROR);
        }
    }

    @NodeIntrinsic(value = ForeignCallNode.class)
    public static native void enableReservedZone(@ConstantNodeParameter ForeignCallDescriptor enableReservedZone, Word thread);

    public static class Templates extends AbstractTemplates {

        public Templates(HotSpotProviders providers, TargetDescription target) {
            super(providers, providers.getSnippetReflection(), target);
        }

        private final SnippetInfo checkReservedAndThrow = snippet(ReservedStackOverflowSnippets.class, "checkReservedAndThrow");

        public void lower(ReservedStackAccessCheckNode reservedStackCheck, HotSpotRegistersProvider registers, LoweringTool tool) {
            Arguments args = new Arguments(checkReservedAndThrow, reservedStackCheck.graph().getGuardsStage(), tool.getLoweringStage());
            args.addConst("threadRegister", registers.getThreadRegister());
            args.addConst("sp", registers.getStackPointerRegister());
            args.addConst("javaThreadReservedStackActivationOffset", HotSpotHostForeignCallsProvider.getJavaThreadReservedStackActivationOffset(HotSpotVMConfig.config()));
            template(args).instantiate(providers.getMetaAccess(), reservedStackCheck, DEFAULT_REPLACER, args);
        }
    }
}
