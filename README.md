Refer to iOS version:
[HLImages](https://github.com/lightonalan/HLImageTag) <br>
**Feature**
- Tag an user to image with finger tap.
- Move TagView when overlap.
- Remove tag.
![alt text](https://github.com/lightonalan/TagView/blob/master/Screenshot.png) <br> <br>
**Usage** <br>
Inherit ImageTagViewTapped interface from Activity <br>
```
class MainActivity : AppCompatActivity(), ImageTagViewTapped 
```
Create ImageTagView  <br>
```
val img = ImageTagView(this)
img.delegate = this //important
img.canAddOrMoveTag = true //allow user tag or not, defalut = true
ll_main.addView(img)
```
Override tapAt function to detect user tap position <br>
```
override fun tapAt(point: Point, imageTag: ImageTagView) {
    //Create fake user or redirect to Search User Function
    var user = TagUser()
    user.tagId = (imageTag.tags.size + 1).toString()
    user.userId = (imageTag.tags.size + 1).toString()
    user.userName = "タグを付ける"
    user.point = point
    imageTag.addTag(user)
}
```
