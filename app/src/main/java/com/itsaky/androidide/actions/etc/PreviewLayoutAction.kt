/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.actions.etc

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.android.aaptcompiler.AaptResourceType.LAYOUT
import com.android.aaptcompiler.extractPathData
import com.blankj.utilcode.util.KeyboardUtils
import com.itsaky.androidide.EditorActivity
import com.itsaky.androidide.actions.ActionData
import com.itsaky.androidide.actions.EditorRelatedAction
import com.itsaky.androidide.resources.R
import com.itsaky.androidide.editor.ui.IDEEditor
import com.itsaky.androidide.uidesigner.UIDesignerActivity
import java.io.File

/** @author Akash Yadav */
class PreviewLayoutAction() : EditorRelatedAction() {

  override val id: String = "editor_previewLayout"

  constructor(context: Context) : this() {
    label = context.getString(R.string.title_preview_layout)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_preview_layout)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    val viewModel = getActivity(data)!!.viewModel
    if (viewModel.isInitializing.value == true) {
      visible = true
      enabled = false
      return
    }

    if (!visible) {
      return
    }

    val editor = data.requireEditor()
    val file = editor.file!!

    val isXml = file.name.endsWith(".xml")

    val type = extractPathData(file).type

    visible = isXml && type == LAYOUT
    enabled = visible
  }

  override fun getShowAsActionFlags(data: ActionData): Int {
    val activity = getActivity(data) ?: return super.getShowAsActionFlags(data)
    return if (KeyboardUtils.isSoftInputVisible(activity)) {
      MenuItem.SHOW_AS_ACTION_IF_ROOM
    } else {
      MenuItem.SHOW_AS_ACTION_ALWAYS
    }
  }

  override fun execAction(data: ActionData): Boolean {
    val activity = getActivity(data)!!
    activity.saveAll()
    activity.previewLayout(data.requireEditor().file!!)
    return true
  }

  private fun EditorActivity.previewLayout(file: File) {
    val intent = Intent(this, UIDesignerActivity::class.java)
    intent.putExtra(UIDesignerActivity.EXTRA_FILE, file.absolutePath)
    uiDesignerResultLauncher.launch(intent)
  }

  private fun ActionData.requireEditor(): IDEEditor {
    return getEditor(this)
      ?: throw IllegalArgumentException("An editor instance is required but none was provided")
  }
}
