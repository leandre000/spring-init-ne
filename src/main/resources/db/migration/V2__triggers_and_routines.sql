-- =============================================================================
-- V2 — Database Triggers & Routines
-- =============================================================================

-- 1. Trigger Function for Bill Creation Notification
CREATE OR REPLACE FUNCTION trg_after_bill_insert()
RETURNS TRIGGER AS $$
DECLARE
    v_customer_name VARCHAR;
BEGIN
    -- Resolve customer name
    SELECT full_name INTO v_customer_name FROM customers WHERE id = NEW.customer_id;
    
    -- Insert notification record
    INSERT INTO notifications (customer_id, message, status, created_at)
    VALUES (
        NEW.customer_id,
        'Dear ' || COALESCE(v_customer_name, 'Customer') || ', Your ' || NEW.billing_month || '/' || NEW.billing_year || ' utility bill of ' || NEW.total_amount || ' FRW has been successfully processed.',
        'PENDING',
        NOW()
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Bind Bill Creation Trigger
CREATE TRIGGER bill_created_trigger
AFTER INSERT ON bills
FOR EACH ROW
EXECUTE FUNCTION trg_after_bill_insert();


-- 2. Trigger Function for Payment Receipt & Status Updates
CREATE OR REPLACE FUNCTION trg_after_payment_insert()
RETURNS TRIGGER AS $$
DECLARE
    v_customer_name VARCHAR;
    v_customer_id BIGINT;
    v_current_paid DECIMAL(12, 2);
    v_total_amount DECIMAL(12, 2);
    v_new_paid DECIMAL(12, 2);
    v_new_balance DECIMAL(12, 2);
    v_bill_status VARCHAR;
BEGIN
    -- Fetch the targeted bill details
    SELECT customer_id, total_amount, paid_amount INTO v_customer_id, v_total_amount, v_current_paid
    FROM bills WHERE id = NEW.bill_id;
    
    -- Calculate updated amounts
    v_new_paid := COALESCE(v_current_paid, 0.00) + NEW.amount_paid;
    v_new_balance := v_total_amount - v_new_paid;
    
    -- Determine new billing status
    IF v_new_balance <= 0.00 THEN
        v_bill_status := 'PAID';
        v_new_balance := 0.00;
    ELSIF v_new_paid > 0.00 THEN
        v_bill_status := 'PARTIAL';
    ELSE
        v_bill_status := 'PENDING';
    END IF;
    
    -- Update the bill details
    UPDATE bills 
    SET paid_amount = v_new_paid,
        balance = v_new_balance,
        status = v_bill_status,
        updated_at = NOW()
    WHERE id = NEW.bill_id;
    
    -- Resolve customer name
    SELECT full_name INTO v_customer_name FROM customers WHERE id = v_customer_id;
    
    -- Insert payment receipt notification
    INSERT INTO notifications (customer_id, message, status, created_at)
    VALUES (
        v_customer_id,
        'Dear ' || COALESCE(v_customer_name, 'Customer') || ', Your payment of ' || NEW.amount_paid || ' FRW has been received successfully.',
        'PENDING',
        NOW()
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Bind Payment Trigger
CREATE TRIGGER payment_received_trigger
AFTER INSERT ON payments
FOR EACH ROW
EXECUTE FUNCTION trg_after_payment_insert();
