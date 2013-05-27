package org.fedoraproject.mobile

import android.app.Activity
import android.content.{ Context, Intent }
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.{ ActionBarDrawerToggle, FragmentActivity }
import android.view.{ LayoutInflater, View, ViewGroup }
import android.widget.{ AdapterView, ArrayAdapter, ImageView, LinearLayout, TextView }

trait NavDrawerActivity extends FragmentActivity with TypedActivity {

  val navMap = Map(
    "Infrastructure Status" -> (classOf[StatusActivity], R.drawable.ic_menu_star),
    "Package Search" -> (classOf[PackageSearchActivity], R.drawable.ic_menu_search)
  )

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.navdrawer)
  }

  lazy val drawerLayout = findView(TR.drawer_layout)
  lazy val drawerToggle = new ActionBarDrawerToggle(
    this,
    drawerLayout,
    R.drawable.ic_drawer,
    R.string.open,
    R.string.close) {
    override def onDrawerClosed(view: View) = invalidateOptionsMenu
    override def onDrawerOpened(view: View) = invalidateOptionsMenu
  }

  def setUpNav(layout: Int) {
    val fragment = new RootFragment(Option(layout))
    getSupportFragmentManager
      .beginTransaction
      .replace(R.id.content_frame, fragment)
      .commit

    val drawerList = findView(TR.left_drawer)

    //drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

    getActionBar.setDisplayHomeAsUpEnabled(true);
    getActionBar.setHomeButtonEnabled(true);

    class NavAdapter(
      context: Context,
      resource: Int,
      items: Array[String])
      extends ArrayAdapter[String](context, resource, items) {
      override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
        val key = getItem(position)
        val value = navMap(key)

        val layout = LayoutInflater.from(context)
          .inflate(R.layout.drawer_list_item, parent, false)
          .asInstanceOf[LinearLayout]

        layout
          .findViewById(R.id.icon)
          .asInstanceOf[ImageView]
          .setImageResource(value._2)

        layout
          .findViewById(R.id.title)
          .asInstanceOf[TextView]
          .setText(key)

        layout
      }
    }

    drawerList.setAdapter(
      new NavAdapter(
        this,
        android.R.layout.simple_list_item_1,
        navMap.keys.toArray))

    drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener {
      def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        val keys = navMap.keys.toArray
        val intent = new Intent(NavDrawerActivity.this, navMap(keys(position))._1)
        startActivity(intent)
      }
    })
  }

  override def onPostCreate(bundle: Bundle) {
    super.onPostCreate(bundle)
    drawerLayout.setDrawerListener(drawerToggle)
    drawerToggle.syncState
  }

  override def onConfigurationChanged(config: Configuration) {
    super.onConfigurationChanged(config)
    drawerToggle.onConfigurationChanged(config)
  }
}