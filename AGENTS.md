# Personal Blog System - Project Instructions

## Java 编码规范指南 (Project Style Guide)

所有生成的 Java 代码或代码审计必须严格遵守以下规则：

### 1. 类级别注释 (Class-Level)
- **必须** 位于 package 声明之后，class 定义之前。
- **必须** 包含以下 Javadoc 标签：
  - `@author`: 开发者姓名
  - `@date`: 创建日期 (格式: YYYY-MM-DD)
  - **描述**: 一段简短且清晰的中文描述，说明该类的职责。

### 2. 方法级别注释 (Method-Level)
- **所有公有 (public) 和保护 (protected) 方法** 必须有 Javadoc。
- **内容要求**:
  - 第一行是方法的功能描述。
  - `@param`: 每个参数必须有描述。
  - `@return`: 必须描述返回值的含义（void 除外）。
  - `@throws`: 如有受检异常，必须说明触发条件。

### 3. 代码风格示例
/**
 * @description: 订单处理服务类
 * @author czf
 * @date 2026-03-18
 */
public class OrderService {
    /**
     * 根据订单 ID 查询订单详情
     * * @param orderId 订单唯一标识
     * @return 订单实体对象，若未找到则返回 null
     */
    public Order getOrderById(String orderId) { ... }
}


### 4. 异常处理

- catch中不允许直接抛出RuntimeException异常，应该抛出与业务相关的异常，并说明原因。更不能直接抛出Throwable和Exception异常
- 不允许在 catch 块中直接return，如果出现异常应该抛出合理的业务异常
- 不允许在 finally 语句中使用 return


