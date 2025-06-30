from lxml import etree
from time import sleep
from app01.utils.utils_func import draw_num, avoid_check
import time
import random

def crawler(goods_word):
    goods_info = []
    bro = avoid_check()
    bro.get('https://suning.com/')
    
    # 标签定位
    search_input = bro.find_element('id', value='searchKeywords')
    # 搜索关键词
    search_input.send_keys(goods_word)
    # 点击搜索按钮
    btn = bro.find_element('id', value='searchSubmit')
    btn.submit()
    sleep(random.uniform(2, 4))
    
    # 检查页面类型
    page_type = "product"  # 默认商品页
    if "brand.suning.com" in bro.current_url:
        page_type = "brand"
    
    # 执行滚动加载
    for i in range(1, 3):
        bro.execute_script('window.scrollTo(0,document.body.scrollHeight)')
        sleep(random.uniform(2, 3))
    
    # 数据解析
    tree = etree.HTML(bro.page_source)
    
    # 根据页面类型获取商品列表
    if page_type == "product":
        goods_li_list = tree.xpath('//div[@id="product-list"]/ul/li')
    else:  # 品牌页
        goods_li_list = tree.xpath('//div[contains(@class, "item-list")]/ul/li')
    
    # 翻页处理
    for i in range(1, 3):
        try:
            if page_type == "product":
                btn_next = bro.find_element('id', value='nextPage')
            else:
                btn_next = bro.find_element('xpath', '//a[contains(text(),"下一页")]')
            
            url = btn_next.get_attribute('href')
            bro.get(url)
            sleep(random.uniform(2, 4))
            
            # 滚动加载
            for j in range(1, 3):
                bro.execute_script('window.scrollTo(0,document.body.scrollHeight)')
                sleep(random.uniform(2, 3))
            
            # 解析新页面
            tree = etree.HTML(bro.page_source)
            if page_type == "product":
                new_items = tree.xpath('//div[@id="product-list"]/ul/li')
            else:
                new_items = tree.xpath('//div[contains(@class, "item-list")]/ul/li')
            
            goods_li_list.extend(new_items)
        except Exception as e:
            print(f"翻页失败: {str(e)}")
            break
    
    # 解析商品信息
    for li in goods_li_list:
        try:
            # 商品图片
            if page_type == "product":
                goods_img = li.xpath('.//div[@class="img-block"]/a/img/@src')[0]
            else:
                goods_img = li.xpath('.//div[contains(@class, "img-block")]/a/img/@src')[0]
            
            goods_img = 'https:' + goods_img if not goods_img.startswith('http') else goods_img
            
            # 商品标题
            if page_type == "product":
                goods_title = ''.join(li.xpath('.//div[@class="title-selling-point"]/a//text()')).replace('\n', '').strip()
            else:
                goods_title = ''.join(li.xpath('.//div[contains(@class, "title-selling-point")]/a//text()')).replace('\n', '').strip()
            
            # 商品价格
            if page_type == "product":
                price_elem = li.xpath('.//div[@class="price-box"]//text()')
            else:
                price_elem = li.xpath('.//div[contains(@class, "price-box")]//text()')
            
            goods_price = ''.join([p.strip() for p in price_elem if p.strip()])
            goods_price = draw_num(goods_price)
            if not goods_price:
                continue
            
            # 销量
            if page_type == "product":
                goods_sales = li.xpath('.//div[@class="info-evaluate"]/a/i/text()')
            else:
                goods_sales = li.xpath('.//div[contains(@class, "info-evaluate")]/a/i/text()')
            
            goods_sales = goods_sales[0] if goods_sales else '0'
            
            # 店铺名
            if page_type == "product":
                shop_elem = li.xpath('.//div[@class="store-stock"]/a/text()')
            else:
                shop_elem = li.xpath('.//div[contains(@class, "store-stock")]/a/text()')
            
            goods_shop = shop_elem[0] if shop_elem else '未知'
            
            # 商品链接
            if page_type == "product":
                link_str = li.xpath('.//div[@class="title-selling-point"]/a/@sa-data')[0]
                link_list = link_str.split(',')
                link_shop_id = draw_num(link_list[2])
                link_prd_id = draw_num(link_list[1])
                goods_link = f'https://product.suning.com/{link_shop_id}/{link_prd_id}.html'
            else:
                goods_link = li.xpath('.//div[contains(@class, "title-selling-point")]/a/@href')[0]
                goods_link = 'https:' + goods_link if not goods_link.startswith('http') else goods_link
            
            # 添加到结果列表
            goods_info.append({
                'goods_img': goods_img,
                'goods_title': goods_title[:127],  # 限制标题长度
                'goods_price': float(goods_price),
                'goods_sales': goods_sales.replace('+', '') if goods_sales else '0',
                'shop_title': goods_shop,
                'shop_platform': '苏宁',
                'goods_link': goods_link,
                'grab_time': time.strftime('%Y-%m-%d %H:%M', time.localtime()),
                'page_type': page_type  # 添加页面类型标识
            })
        except Exception as e:
            print(f"解析商品时出错: {str(e)}")
            continue
    
    sleep(2)
    bro.quit()
    return goods_info

if __name__ == "__main__":
    word = "李宁"
    sn_goods_info = crawler(goods_word=word)
    print(f"共获取 {len(sn_goods_info)} 条商品数据")
    for idx, item in enumerate(sn_goods_info[:3], 1):  # 打印前3条作为示例
        print(f"\n商品 {idx}:")
        print(f"类型: {'商品页' if item['page_type'] == 'product' else '品牌页'}")
        print(f"标题: {item['goods_title']}")
        print(f"价格: {item['goods_price']}")
        print(f"销量: {item['goods_sales']}")
        print(f"店铺: {item['shop_title']}")
        print(f"链接: {item['goods_link']}")