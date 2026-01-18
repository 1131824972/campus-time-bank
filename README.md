⏳ Campus Time Bank | 校园时间银行

基于“时间币”互助模式的校园公益平台 | A Campus Mutual Aid Platform based on "Time Coins"

📖 项目介绍 (Introduction)

校园时间银行 是一个致力于解决高校学生碎片化互助需求的 Web 应用。

本项目采用 前后端分离开发，统一部署 的架构模式。前端基于 Vue 3 生态构建，通过构建步骤集成至 Spring Boot 后端资源目录中，最终打包为独立的 JAR 包运行，实现了开箱即用的便捷体验。

核心理念：让每一次互助都有回响 (Make every help count)。

✨ 核心功能 (Features)

🪙 互助交易系统

发布任务：支持跑腿、辅导、生活三大类任务发布，自定义悬赏金额。

大厅抢单：瀑布流展示实时需求，支持下拉刷新与分类筛选。

订单流转：包含“待接单 -> 进行中 -> 待确认 -> 已完成”的标准状态机。

安全结算：只有发布者确认完成后，资金才会划转，保障双方权益。

🎁 权益激励商城

商品兑换：使用赚取的时间币兑换真实商品。

核销体系：生成唯一核销码 (Code)，支持线下门店核销。

💳 个人信用资产

拟物化存折：精美的 CSS 银行卡效果，展示余额与信用分。

收支明细：每一笔时间币的流入流出均有据可查（Wallet Logs）。

我的履约：查看我发布和参与的历史任务。

🛠 技术栈 (Tech Stack)

前端 (Frontend)

核心框架: Vue 3 (Composition API) + TypeScript

构建工具: Vite

UI 组件库: Vant 4 (移动端适配)

状态管理: Pinia

路由管理: Vue Router 4

HTTP 客户端: Axios

后端 (Backend)

核心框架: Spring Boot 3.0.2

ORM 框架: MyBatis-Plus

数据库: MySQL 8.0

静态资源: 集成 Vue 打包后的静态文件 (SPA)

📂 目录结构 (Directory Structure)

campus-time-bank/
├── src/main/java/           # Java 源代码
├── src/main/resources/
│   ├── mapper/              # MyBatis XML 映射文件
│   ├── static/              # ✨ 前端构建产物 (Vue dist)
│   └── application.yml      # 后端配置
└── pom.xml                  # Maven 依赖管理


注意：前端源代码位于独立的 frontend-src 文件夹（或开发时的独立仓库），构建后的产物 (index.html, assets/) 被复制到了 src/main/resources/static/ 下。

🚀 快速开始 (Getting Started)

无需安装 Node.js 环境，直接运行 Java 后端即可体验完整功能。

准备数据库：创建数据库 time_bank_db 并导入初始化 SQL 脚本。

配置数据库：修改 application.yml 中的 MySQL 账号密码。

启动应用：

# 运行 Spring Boot 主程序
mvn spring-boot:run


访问应用：
打开浏览器访问 http://localhost:8080 (页面已集成在后端中)。
